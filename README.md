### Regarding the license...

The Apache License Version 2.0 licences requires attribution when "redistributing" the code.
I have relaxed this requirement by overriding section `4. Redistribution` clause to be, in totality:

```
   4. Redistribution. You may reproduce and distribute copies of the
      Work or Derivative Works thereof in any medium, with or without
      modifications, and in Source or Object form, WITH NO ATTRIBUTION REQUIREMENTS.
```

This allows you to use the code in any way you see fit without the need to add attribution. 
I literally don't care what you do with this code or if you attribute it to me. If you want to, that's okay too!

-scott

### ByteArrayBuilder

[ByteArrayBuilder.java](src/main/java/io/arondight/ByteArrayBuilder.java)
is an nifty wrapper around java.nio.ByteBuffer. 
It makes it easy to use ByteBuffer but doesn't require you to know how many bytes you need to allocate
since it does the work to grow the buffer as you add data.

### CancellableCountDownLatch

[CancellableCountDownLatch.java](src/main/java/io/arondight/CancellableCountDownLatch.java)
is an improvement on CountDownlatch that adds ability to cancel the count down, immediately freeing the latch.

### GZipper

[GZipper.java](src/main/java/io/arondight/GZipper.java)
is a utility to make gzipping easy.

### Json Reader / Writer

[JsonReader.java](src/main/java/io/arondight/JsonReader.java) and [JsonWriter.java](src/main/java/io/arondight/JsonWriter.java)
are utilities to simplify the common json tasks.

### ParseNumber

[ParseNumber.java](src/main/java/io/arondight/ParseNumber.java)
is a utility for parsing human-readable numbers that might make input easier,

| Example      | Java Long Value |
| --------| ---- |
| 1_000   | 1,000 |
| 10,000  | 10,000 |
| 100.000 | 100,000 |
| 1k      | 1,000 |
| 1ki     | 1,024 |
| 1m      | 1,000,000 |
| 1mi     | 1,048,576 |
| 1g      | 1,000,000_000 |
| 1gi     | 1,073,741,824 |

### SimpleDate

[SimpleDate.java](src/main/java/io/arondight/SimpleDate.java)
is a utility for manipulating a date in simple form i.e. 20100101

### Ulong

[Ulong.java](src/main/java/io/arondight/Ulong.java)
is an object that represents a 64 bit unsigned long
