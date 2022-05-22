# Requirements

- JDK 11 or above
- FFMPEG
- FFPROBE
- Account on [Opensubtitles.com](https://www.opensubtitles.com/)

# Usage

## Workflow

- Download subtitles of the Tv Show which you want to identify ( download multiple samples for best result )
- Load files and click on the identify button
- Check if the result makes sense.
- Rename the files

# Video example

https://user-images.githubusercontent.com/41428663/164988475-cc128593-f85b-4014-8351-23b2bc43797d.mp4

# Building
```
mvn clean compile package
```

# ToDo

- Implement Teseract analysis
- Document and clean up Code
- Implement CLI