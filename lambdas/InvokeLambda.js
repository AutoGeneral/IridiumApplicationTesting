var AWS = require('aws-sdk');
// Set the region
AWS.config.update({region: 'us-east-1'});
var lambda = new AWS.Lambda();

exports.handler = function(event, context, callback) {
	var iridiumParams = {
		FunctionName: 'Iridium',
		InvocationType: 'RequestResponse',
		LogType: 'Tail',
		Payload: JSON.stringify(event.iridiumSettings)
	};

	lambda.invoke(iridiumParams, function(err, data) {
		if (err) {
			callback(err);
		} else {
			var dynamoDBParams = {
				FunctionName: 'IridiumResults',
				InvocationType: 'RequestResponse',
				LogType: 'Tail',
				Payload: JSON.stringify({
					table: "IridiumResults",
					testName: event.testName,
					result: data.Payload
				})
			};

			lambda.invoke(dynamoDBParams, function(err, data) {
				if (err) {
					callback(err);
				} else {
					callback(null, "Ran Iridium and saved results");
				}
			});
		}
	})
};
