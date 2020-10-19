Java library for the Presentation Graphic Stream format (SUP files)
====
- Saves all Segments into Java Classes
- Converts SUP File -> JSON
- Saves Subtitles as PNG Images
___
Credits: [Protocol overview](http://blog.thescorpius.com/index.php/2017/07/15/presentation-graphic-stream-sup-files-bluray-subtitle-format/)

---
```java
        Path pathToSupFile = Paths.get("test.sub");        
        Sup sup = new Sup(path);
        sup.toJsonWithoutImages("jsonFile"); // without .json Extension
        sup.toJson("jsonFile2"); // saves Images in Folder ./jsonFile2_images/

        PGS[] segments = sup.getSegments();
        int pTime = segments[0].presentationTimestamp;
```