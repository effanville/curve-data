# README

A small collection of classes for storing curve data, and allowing for
curve data to be retrieved.

The primary class to use is the `Curve` interface that allows for retrieving the volume between
two times using the `getVolume(start, end)`
method. A further method to get normalised volume at a time from the volume between two other times,
`getRelativeVolume(start, end, time)` is also provided.

This curve class also provides basic validation exposed through the `isValid` method that verifies
if the curve buckets are well formed (positive volume and start before end) and that the curve as a
whole is well formed (volume = 100, no overlapping buckets).

A class `CurveCsvReader` is provided that allows for reading a `Curve` from a csv file where the file
has schema

```
startTime,endTime,volume,bucketType
```

This code is all used in a sample main method in the `App` class.

## Execution

To execute the example provided, first build using

```
mvn clean install
```

and then run with working directory the root of the repo the following:

```
java -jar target/curvedata-1.0.0.jar

```

## Assumptions

The curves here assume that the total timespan is within the same day, as the `LocalTime` class doesn't
allow for times over a single day.

Further, we also assume that there are no other bucket types other than those given in the `BucketType`
enum.

We also implicitly assume that each curve is specified solely by the `symbol`. In practise this is an
insufficient identifier, as the curve could depend upon the time the volume curve was an average over
(e.g. 1 week or 2 week curve), as well as whether the curve was for special days or not. This would be 
a straightforward enhancement, but providing such information was beyond the scope of what was required. 

We do not assume, however, that the curve is specified by buckets of fixed time length.

## Other information

Further information on the original [spec](doc/spec.md) can be found at the link.