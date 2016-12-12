# confusionmatrix
This repository contains an Eclipse Java project that is useful for generating accuracy, precision, and recall metrics 
for [Watson Conversation Service](http://www.ibm.com/watson/developercloud/conversation.html). The code also generates
the confusion matrix to help better understand the quality of your trained [Watson Conversation Service]
(http://www.ibm.com/watson/developercloud/conversation.html).

For further details on the process to train your chatbot, please refer to this [blog](https://developer.ibm.com/dwblog/2016/10-steps-train-chat-bot-chatbot-machine-learning/).

# Running Locally
To run the code locally:
-	Start Eclipse
-	Go to **File -> Import**
-	Choose **Git -> Projects from Git** and press **Next**
-	Choose **Clone URI** and press **Next**
-	In the URI field, paste the link to github repo and press **Next**:
  **https://github.com/joekozhaya/confusionmatrix.git**
-	Select **master** and press **Next**
-	Select Directory on your local machine where repository will be cloned and press **Next**
  **Directory: ~/git/confusionmatrix**
  **Initial branch: master**
  **Remote name: origin**
-	Choose **Import existing Eclipse projects** and press **Next**
-	Select **confusionMatrix** project and press **Finish**
 This should clone the project from github repository to your local disk and load the project into Eclipse.
-	In your Package Explorer view in Eclipse, select the **confusionMatrix** project, right click and select 
  **Maven --> Update Project**. Then select the **confusionMatrix** project in the pop-up window and press **OK**.

-	Select **confusionMatrix** project, right click and select **Run As** and select **Run Configurations**. 
- Provide as argument a [Java properties file](/sample.properties) and press **Run**

The properties file should define variables such as the conversation url (conv_url), userid and userpass (these are the username and password from your instance of Watson Conversation Service), numIntents (the number of intents defined in your Watson Conversation Service workspace), [test_csv_filename](/sampleTest.csv?raw=true) (csv file which includes test data) and confmatrix_filename (csv file to write the results to).

### Sample Properties file ###
- conv_url=https://gateway.watsonplatform.net/conversation/api/v1/workspaces/WORKSPACE_ID/message?version=2016-07-11
- userid=YOUR_WATSON_CONVERSATION_SERVICE_username
- userpass= YOUR_WATSON_CONVERSATION_SERVICE_password
- numIntents=NUMBER_OF_INTENTS
- test_csv_filename=COMPLETE_PATH_TO_YOUR_CSV_TEST_FILE
- confmatrix_filename=COMPLETE_PATH_TO_YOUR_CSV_CONFUSIONMATRIX_RESULTS_FILE

To get conv_url, userid, userpass, and numIntents:
-	Log into your Bluemix account
-	Go to your Dashboard and select the conversation service you’d like to test
-	Select Service Credentials and that gives you username (userid) and password (userpass)
-	Select Manage and press Launch Tool  this opens the Workspace view
-	Select the workspace that includes the conversation intents you’d like to test (hit the menu 3 dots and select “View details”)
-	Go back and click on the workspace to open it and go to Intents tab to get the number of intents defined (if you don’t know those).

## License ##
This sample code is provided as-is and licensed under Apache 2.0. Full license text is available in [LICENSE](/License.md).
Code is intended to help with computing the confusion matrix, accuracy, precision, and recall of the intents classification in a Watson Conversation system.
