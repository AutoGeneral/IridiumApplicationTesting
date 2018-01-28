var AWS = require('aws-sdk');
// Set the region
AWS.config.update({region: 'us-east1'});
var lambda = new AWS.Lambda();

exports.handler = function(event, context) {
	var iridiumParams = {
		FunctionName: 'Iridium',
		InvocationType: 'RequestResponse',
		LogType: 'Tail',
		Payload: event.iridiumSettings
	};

	lambda.invoke(iridiumParams, function(err, data) {
		if (err) {
			context.fail(err);
		} else {
			var dynamoDBParams = {
				FunctionName: 'IridiumResults',
				InvocationType: 'RequestResponse',
				LogType: 'Tail',
				Payload: {
					testName: event.testName,
					result: data.Payload
				}
			};

			lambda.invoke(dynamoDBParams, function(err, data) {
				if (err) {
					context.fail(err);
				} else {
					context.success("Ran Iridium and saved results");
				}
			};
		}
	})
};
