// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region
AWS.config.update({region: 'us-east1'});

// Create the DynamoDB service object
ddb = new AWS.DynamoDB({apiVersion: '2012-10-08'});

exports.handler = function(event, context) {
	var params = {
		TableName: event.table,
		Item: {
			'TestName': event.testName,
			'Result': event.result
		}
	};

	// Call DynamoDB to add the item to the table
	ddb.putItem(params, function (err, data) {
		if (err) {
			console.log("Error", err);
		} else {
			console.log("Success", data);
		}
	});
};
