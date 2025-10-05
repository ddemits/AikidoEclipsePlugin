# AikidoEclipsePlugin
Eclipse plugin for the Aikido recruitment process.

## Installation
1. Download the JAR from the releases page.  
2. Copy it to the `dropins` folder in your Eclipse installation.  
3. Restart Eclipse.  

> **Note:** Without the mock server, the plugin cannot fetch data (see below).

## Development Setup

**Mock Server**

Run the following command in the server folder:

```
    ./gradlew bootRun
```

**Plugin**
1. Import the `plugin` folder in Eclipse: **File > Open Projects from File System**.  
2. Add all JARs from `/lib` and `/test-lib` to the project's **Java Build Path > Libraries**.  
3. Right-click the project and select **Run As > Eclipse Application**.

## Next Steps
- Field 'lastUpdated' is handled as a String. Ideally this is parsed to ZonedDateTime. This would allow us to display the date in the time zone of the user. 
- Currently dependencies are commited in the repository and need to be manually added and configured in Eclipse. Ideally a dependency manager handles this.
- Implement a way for the user to interrupt the fetch operation.

## TODO
- When reviewing the requirements I noticed that errors should be visible in the Results view. Currently this is communicated to the user with a pop-up. I had no time to change this.
