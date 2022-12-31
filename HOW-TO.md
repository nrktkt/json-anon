# How-To

## Creating a basic schema for an object

Creating a schema is as simple as identifying all the fields that could be in an object and 
choosing what order to put them in. 

For example if we have a person object with fields like
* `firstName` - string
* `lastName` - string
* `middleName` - optional string
* `phoneNumber` - optional string
* `age` - number

Our schema would simply be putting those field names in an array like 

```json
["firstName", "middleName", "lastName", "phoneNumber", "age"]
```

> note the schema doesn't actually care about the field types

## Creating schemas with nested objects

Fields with object values work automatically, they simply won't be modified.   

For example the schema

```json
["name", "address"]
```

applied to the object

```json
{
  "name": "Alice",
  "address": {
    "street": "1060 W Addison St",
    "city": "Chicago",
    "state": "IL",
    "country": "USA"
  }
}
```

would produce 

```json
[
  "Alice", 
  {
    "street": "1060 W Addison St",
    "city": "Chicago",
    "state": "IL",
    "country": "USA"
  }
]
```

Works fine, but not much space savings

### Nested schemas

However if we create an address schema

```json
["street", "city", "state", "country"]
```

we can add a name to make it a field schema

```json
{ "address": ["street", "city", "state", "country"] }
```

which can be added to our person schema

```json
[
  "name",
  { "address": ["street", "city", "state", "country"] }
]
```

to get a more compact result

```json
[
  "Alice", 
  ["1060 W Addison St", "Chicago", "IL", "USA"]
]
```

## Re-using schemas

Inevitably we'll want to use a schema in more than one place, maybe even recursively.  
To do this we'll need to name the schema and put it in a "root object".   
For example a simple schema for a person with just a name

```json
{
  "person": ["name"]
}
```

If we want to add children to the person schema, we can now refrence it by its name (`person`).

```json
{
  "person": [
    "name",
    "children": {"type": "array", "schema": "__$//person"}
  ]
}
```

## Adding, Renaming, or Deprecating fields in a schema

### Adding fields

Adding a field is as simple as appending a field name to the schema.

from

```json
["name"]
```

to 

```json
["name", "age"]
```

Old producers of objects will have the new field absent, so new consumers should be able to provide a default or handle the absence. 

Old consumers of objects will ignore the new field, so consumers should be updated before behavior-changing fields are added.

### Renaming fields

Renaming fields is trivial. Names can be changed in-place without old producers needing to know about it.  
But the meaning of the field must not change.

### Deprecating fields

To deprecate a field, just use `null` instead of a schema in the same position.

For example deprecating a `name` field and splitting it into two new fields might look like

```json
["name", "age"]
```

to

```json
[null, "age", "firstName", "lastName"]
```

New consumers will ignore deprecated fields. 
So a compact object

```json
["Alice Smith", {}, "Alice", "Smith"]
```

would become

```json
{"firstName": "Alice", "lastName": "Smith"}
```

