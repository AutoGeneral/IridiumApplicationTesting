package au.com.agic.apptesting.utils.impl;

import static au.com.agic.apptesting.utils.JarDownloader.LOCAL_JAR_FILE_SYSTEM_PROPERTY;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.FileSystemUtils;
import au.com.agic.apptesting.utils.SystemPropertyUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.validation.constraints.NotNull;

import javaslang.control.Try;

/**
 * An implementation of the FileSystemsUtils service
 */
public class FileSystemUtilsImpl implements FileSystemUtils {

	private static final String REPORT_DIR = "WebAppTestingReports";
	private static final String DATE_FORMAT = "yyyyMMdd";
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();

	@Override
	public String buildReportDirectoryName() {
		/*
			Return the local directory if we are not saving in the users home folder
		 */
		final String saveInHomeDir = StringUtils.defaultIfBlank(
			SYSTEM_PROPERTY_UTILS.getProperty(Constants.SAVE_REPORTS_IN_HOME_DIR),
			Boolean.TRUE.toString());

		if (!Boolean.parseBoolean(saveInHomeDir)) {
			return ".";
		}

		/*
			The base directory for all reports
		*/
		final String homeDirString = System.getProperty("user.home") + File.separator + REPORT_DIR;

		/*
			Make sure this directory exists
		*/
		final File homeDir = new File(homeDirString);
		if (!homeDir.exists()) {
			homeDir.mkdirs();
		}

		/*
			Individual test runs will be saved in a folder by date
		*/
		final String dateAsString = new SimpleDateFormat(DATE_FORMAT).format(new Date());

		/*
			These folders will have an optional number suffix to make them unique
		*/
		String suffix = "";
		int count = 0;

		/*
			Keep checking until we find something unique
		*/
		while (new File(homeDirString + File.separator + dateAsString + suffix).exists()) {
			++count;
			suffix = "(" + count + ")";
		}

		/*
			This is the folder that will hold the reports
		*/
		return homeDirString + File.separator + dateAsString + suffix;
	}

	@Override
	public void copyFromJar(
			@NotNull final String sourcePath,
			@NotNull final Path target) throws IOException {
		checkArgument(StringUtils.isNotBlank(sourcePath));
		checkNotNull(target);

		copyFromJar(URI.create(sourcePath), target);
	}

	@Override
	public void copyFromJar(@NotNull final URI sourcePath, @NotNull final Path target) throws IOException {
		checkNotNull(sourcePath);
		checkNotNull(target);

		final PathReference pathReference = getPath(sourcePath);
		final Path jarPath = pathReference.getPath();

		Files.walkFileTree(jarPath, new SimpleFileVisitor<Path>() {

			private Path currentTarget;

			@Override
			public FileVisitResult preVisitDirectory(
					final Path dir,
					final BasicFileAttributes attrs) throws IOException {
				currentTarget = target.resolve(jarPath.relativize(dir)
					.toString());
				Files.createDirectories(currentTarget);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(
					final Path file,
					final BasicFileAttributes attrs) throws IOException {
				Files.copy(file, target.resolve(jarPath.relativize(file)
					.toString()), StandardCopyOption.REPLACE_EXISTING);
				return FileVisitResult.CONTINUE;
			}

		});
	}

	/**
	 * http://stackoverflow.com/questions/24000812/read-from-file-system-via-filesystem-object
	 */
	private static PathReference getPath(final URI resPath) throws IOException {
		return Try.of(() -> new PathReference(Paths.get(resPath), null))
			.orElse(() -> Try.of(() -> {
				final Map<String, ?> env = Collections.emptyMap();
				final FileSystem fs = FileSystems.newFileSystem(resPath, env);
				return new PathReference(fs.provider().getPath(resPath), fs);
			}))
			.orElse(() -> Try.of(() -> {
				/*
					If we get here, it means that we are running from a web start jar,
					which looks like:
					jar:https://s3-ap-southeast-2.amazonaws.com/ag-iridium/webapptesting-signed.jar!/zap

					getSchemeSpecificPart() will return:
					https://s3-ap-southeast-2.amazonaws.com/ag-iridium/webapptesting-signed.jar!/zap
				 */
				final URI newUri = URI.create(resPath.getSchemeSpecificPart());

				/*
					Copy it somewhere locally
				 */
				final File copy = new File(System.getProperty(LOCAL_JAR_FILE_SYSTEM_PROPERTY));

				/*
					Recreate the reference to the local copy of the jar file
				 */
				final URI newResPath = new URIBuilder()
					.setScheme("jar")
					.setPath(copy.toURI()
						+ newUri.getPath().replaceAll("^.*?(?=\\!)", ""))
					.build();

				final Map<String, ?> env = Collections.emptyMap();
				final FileSystem fs = FileSystems.newFileSystem(newResPath, env);
				return new PathReference(fs.provider().getPath(newResPath), fs);
			}))
			.getOrElseThrow(x -> new IOException("Could not process the URI " + resPath, x));
	}

	/**
	 * http://stackoverflow.com/questions/24000812/read-from-file-system-via-filesystem-object
	 */
	private static class PathReference implements AutoCloseable {
		private final FileSystem fileSystem;
		private final Path path;

		PathReference(final Path path, final FileSystem fileSystem) {
			this.fileSystem = fileSystem;
			this.path = path;
		}

		@Override
		public void close() throws Exception {
			if (fileSystem != null) {
				fileSystem.close();
			}
		}

		public Path getPath() {
			return path;
		}

		public FileSystem getFileSystem() {
			return fileSystem;
		}
	}
}
