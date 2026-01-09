# FixParser

## Problem Statement

Write a Java parser to parse FIX messages. Design an API so people can easily access the fields of a FIX message. 
The input of your parser is a byte[], and you can assume it contains at least one complete FIX message, and the 
first bye of the FIX message is at position 0.


## Brief Design Document

The parser is designed to be lightweight and efficient for parsing FIX messages.
It runs a single pass on the array to extract FIX tag and location of their values from ASCII byte array.
The tag and value location are stored in a Map for easy retrieval of value for a given tag.
It provides helper methods to retrieve value as byte array, Integer, Long, Double and byte.

It also provides methods to validate the FIX message for required tags and checksum validation.

### Assumptions and Limitations
1. The parser assumes that the input byte array contains at least one complete FIX message starting at position
2. The message format is assumed to be in ASCII format as it provides most efficient storage and transmission
3. Classes are built assuming they will be run in Thread safe process
4. The code throws exception where needed creating garbage. This also provides reason to not log at the parser level.
5. The parser currently does not handle repeating groups in FIX messages

### Future Enhancements
1. Enhance the parser to handle repeating groups in FIX messages
2. Implement more comprehensive validation for FIX messages

### Data-structure
It has a custom implementation of Map to store tag and value location in the byte array.
This helps to ensure garbage is not creating during operations.


### Important Classes
1. FixParser - Main class to parse FIX message from byte array and provide methods to access tag values
2. ByteUtils - Utility class to perform byte array operations
3. FieldLocationMap - Custom implementation of Map to store tag and value location


### System Requirements
1. Minimum JDK version 1.7, 1.8 preferred as it is configured at project level
2. Minimum JUnit 4, JUnit 5 preferred as it is configured at project level 