# json-anon

Given some schema for an object, you could "anonymize it" into an array by removing the field names. On the other end, you could de-anonymize an array into an object given a schema. 
ie.
```json
{ "version": "00", "trace-id": "4bf92f3577b34da6a3ce929d0e0e4736", "parent-id": "00f067aa0ba902b7", "trace-flags": "01" }
```
\-
```json
["version", "trace-id", "parent-id", "trace-flags"]
```
 =
```json
["00", "4bf92f3577b34da6a3ce929d0e0e4736", "00f067aa0ba902b7", "01"]
```
 \+
```json
["version", "trace-id", "parent-id", "trace-flags"]
```
 =
```json
{ "version": "00", "trace-id": "4bf92f3577b34da6a3ce929d0e0e4736", "parent-id": "00f067aa0ba902b7", "trace-flags": "01" }
```

## Specification

### Definitions

**Name**: the name in the name/value pair of an elment of a JSON object.

**Schema**: an array with elements which are either a name, schema reference, or a schema.

**Schema reference**: an object with one entry, the key is the field name, the value (must be string) is the name of a schema. 

**Value Array**: an JSON object which has been anonymized into an array containing only the values from the object (no keys).

### Anonymizing JSON

Given a schema and a JSON object, create a new array and append the values from the object in the order their names appear in the schema. If a name is present in the schema but not in the object, use an empty object instead. If a schema element contains a nested schema or a schema reference, use that to anonymize the value before adding it to the value array.

### Deanonymizing JSON

Given a schema and a value array, for each element in the schema, add the name to a new object using the value at the corresponding index in the value array. If the value is an empty object, do not add it to the output object. If the schema element is a nested schema or schema reference, use that schema to deanonymize the value before adding it to the output object.
