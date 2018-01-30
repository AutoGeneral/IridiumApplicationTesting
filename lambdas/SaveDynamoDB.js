// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region
AWS.config.update({region: 'us-east-1'});

// Create the DynamoDB service object
const ddb = new AWS.DynamoDB({apiVersion: '2012-10-08'});

exports.handler = function(event, context, callback) {
	var params = {
		TableName: event.table,
		Item: {
			'TestName': {S: event.testName},
			'Result': {S: event.result}
		}
	};

	// Call DynamoDB to add the item to the table
	ddb.putItem(params, function (err, data) {
		if (err) {
			callback(err);
		} else {
			callback(null, "Success");
		}
	});
};
