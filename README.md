# json-anon

## Schemas

```
 array──┬──►simple
        │
 object─┴──►reference
```

Schemas are simple objects with two fields

```
{
"type":   ...,
"schema": ...
}
```

`type` indicates the type of schema, possible values are `simple`, `array`, `object`, and `reference`.  
`schema` provides the information about how to hydrate the value. Its value depends on the type.  
In addition, some schemas have a compact format for ease of human reading and writing. 
Each schema type is defined in more detail below.

### Simple

| `type`     | `schema` |
| ---------- | -------- |
| `"simple"` | none     |

A simple schema is effectively a no-op. It indicates that the field should be represented in the same in both the hydrated and dehydrated schema.

#### Example

##### Schema
`{"type": "simple"}`
##### Hydrated
`{"foo":"bar"}`
##### Dehydrated
`{"foo":"bar"}`

### Object

| `type`     | `schema` |
| ---------- | -------- |
| `"object"` | array of fields |

The object schema indicates that the field is an object. The schema effectively maps an index to a field name, and optionally provides a schema for the field. This way objects can be converted arrays by removing the field names as well as the other way around.

#### Field

##### Named

A named field is one which has a name and no sub schema. It is a string type and will be the field name when the object is hydrated.

##### Typed

A typed field is one which has a name and a schema. It is an object which must have exactly one field whose value is a schema.

##### Deprecated

Deprecated fields are no longer in use and will be ignored when an object is hydrated or dehydrated. They are represented by a `null` value.

#### Compact format

The compact format of an object schema is simply the value of its schema field.

#### Example

##### Schema
```
{
"type": "object",
"schema: [ {"foo": ["bar", "baz]}, null, "bang" ]
}
```
##### Hydrated
`{
"foo": { "bar": 1, "baz": 5},
"bang": 9
}`
##### Dehydrated

```
[
  [1, 5],
  {}, // todo cannonicallize a value for deprecated fields on dehydration?
  9
]
```

### Array

| `type`     | `schema` |
| ---------- | -------- |
| `"array"`  | schema   |

An array schema indicates that the field is an array and its contents should use the provided schema.

#### Example

##### Schema
```
{
"type": "array",
"schema": ["foo", "baz"]
}
```
##### Hydrated
```
[
  {"foo":"bar"}, 
  {"baz": 5}
]
```
##### Dehydrated
```
[
  ["bar", {}],
  [{}, 5]
]
```

### Reference

| `type`     | `schema` |
| ---------- | -------- |
| `"reference"` | string |

## Root object
