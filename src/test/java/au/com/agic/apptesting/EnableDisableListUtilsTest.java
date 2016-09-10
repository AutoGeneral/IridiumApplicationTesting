package au.com.agic.apptesting;

import au.com.agic.apptesting.utils.EnableDisableListUtils;
import au.com.agic.apptesting.utils.impl.EnableDisableListUtilsImpl;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests of the EnableDisableListUtils service
 */
public class EnableDisableListUtilsTest {
	private static final EnableDisableListUtils ENABLE_DISABLE_LIST_UTILS = new EnableDisableListUtilsImpl();

	@Test
	public void test1() {
		Assert.assertTrue(ENABLE_DISABLE_LIST_UTILS.enabled("option1", "option1"));
		Assert.assertTrue(ENABLE_DISABLE_LIST_UTILS.enabled("option1,option2", "option1"));
		Assert.assertTrue(ENABLE_DISABLE_LIST_UTILS.enabled("option1, option2, , , ", "option1"));
		Assert.assertFalse(ENABLE_DISABLE_LIST_UTILS.enabled("option1", "optionx"));
		Assert.assertFalse(ENABLE_DISABLE_LIST_UTILS.enabled("option1,option2", "optionx"));
		Assert.assertFalse(ENABLE_DISABLE_LIST_UTILS.enabled("option1, option2, , , ", "optionx"));

		Assert.assertTrue(ENABLE_DISABLE_LIST_UTILS.enabled("+option1", "option1"));
		Assert.assertTrue(ENABLE_DISABLE_LIST_UTILS.enabled("+option1,-option2", "option1"));
		Assert.assertTrue(ENABLE_DISABLE_LIST_UTILS.enabled("+option1, -option2, , , ", "option1"));
		Assert.assertFalse(ENABLE_DISABLE_LIST_UTILS.enabled("+option1", "-optionx"));
		Assert.assertFalse(ENABLE_DISABLE_LIST_UTILS.enabled("+option1,-option2", "optionx"));
		Assert.assertFalse(ENABLE_DISABLE_LIST_UTILS.enabled("+option1, -option2, , , ", "optionx"));

		Assert.assertFalse(ENABLE_DISABLE_LIST_UTILS.enabled("-option1", "option1"));
		Assert.assertFalse(ENABLE_DISABLE_LIST_UTILS.enabled("-option1,-option2", "option1"));
		Assert.assertFalse(ENABLE_DISABLE_LIST_UTILS.enabled("-option1, -option2, , , ", "option1"));

		Assert.assertFalse(ENABLE_DISABLE_LIST_UTILS.enabled("-option1", "option1", true));
		Assert.assertFalse(ENABLE_DISABLE_LIST_UTILS.enabled("-option1,-option2", "option1", true));
		Assert.assertFalse(ENABLE_DISABLE_LIST_UTILS.enabled("-option1, -option2, , , ", "option1", true));
	}
}
