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

## Example

```json
{
	"schema1": ["field1", "field2", "field3"],
	"schema2": ["key1", {"key2": "schema1"}, {"key3": ["key3.1", "key3.2"]}]
}
```

```json
[{"foo": "bar"}, [123, "abc", {}], [456, "baz"]]
```
\+

schema2

=
```json
{
"key1": {"foo": "bar"}, 
"key2": {"field1": 123, "field2": "abc"}, 
"key3": {"key3.1": 456, "key3.2": "baz"}
}
```

## Migrations

sometimes you need to change a schema and maintain backwards compatibility. here are recommendations on how to do that.

### Removing a field

Replace the element in the schema with `null`. The field will no longer be included on anonymization, and ignored on deanonymization. 

### Adding a field

Just append an element to the end of the schema. For backwards compatibility the application consuming the deanonymized JSON object should supply a default if needed. 

### Changing a value's schema

For schema entries with a nested schema or schema reference; either

* Apply a backwards compatible migration to the inner schema
* Add the field to the schema again, with the new inner schema at the end. Objects may not have duplcate keys, so implementations must overwrite output entries with the last entry found for that name in the schema. The first entry for that name should be removed in the schema used on for anonymization, so that it does not appear twice in the value array.

### Changing a name

Add the new name as a new field. In the schema used for deanonymization, also update the name in-place for the old field.
