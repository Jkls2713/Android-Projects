## Readme - StackOverflow Challenge

### Functional Requirements:

- You will need to connect to Stackoverflow Users API Endpoint and retrieve the first page of data. FULL API documentation

- Display the retrieved data through a TableView.

- We expect from you to display at least username, badges and gravatar for every user.

- While the gravatar is being downloaded, the UI should show a loading animation.

- Each of the photos should be downloaded only once and stored for offline usage.

- The UI should always be responsive.

### Implementation:

- I designed this app without using any third party libraries to demonstrate my understanding of the native Android Api
	- This also helps keep the app size as small as possible
- I included a search functionality that updates in real time to filter the list of users
- I included a Listview inside the TableLayout to better organize each entry
	- If not for the TableLayout restriction I would have used a recycler view
- Since I used overrides for my implementation it was difficult to add any unit tests
    - However there are UI tests for creation and search functionality
- I implemented a database to hold the different information fields to be used when offline
- I implemented a progress bar to only appear when a task running in background is executed