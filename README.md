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
| `"object"` | array of schemas |

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
"schema": {
  "type": "object",
  "schema": ["foo", "baz"]
  }
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
