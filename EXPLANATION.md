# Explanations

## Motivation

JSON is ubiquitous, easy to read and implement, fast, and rather verbose.  
Most of our JSON data tend to be objects. 
Objects are made up of name-value pairs.
For primitive values the name is roughly half the binary size.   
Therefore if we had a method to create JSON objects without field names, 
we could significantly reduce object size. 
JSON-anon is this method.

## Compared to [JSON Schema](https://json-schema.org/)

## Compared to binary serialization formats (protobuf)

## Absent field placeholder

JSON arrays cannot have empty elements. For example `[1, 2, , 3]` is not a legal array. However objects can have absent fields. Since objects dehydrated with JSON-anon are arrays, we need a way to denote an absent field in the array.   
A number of possibilities occurred

### use `null` (or any other "default" primitive value)
`null` is the first thing most people think of as an empty placeholder, but it has several downsides. 

1. It's not possible to differentiate between a null value and an absent field. In some null-oriented languages this would be fine when creating a native object or struct. But for several JSON protocols the distintion is important and this would render JSON-anon unusuable with those protocols.
2. `null` is 4 bytes in standard UTF-8 JSON, and that's a lot to use for an absent field. 

Other primitives like `0` are unsuitable for some of the reasons above, but even more dangerous. You can imagine an absent `price` field might mean one thing, but the same field non-absent with a value of `0` probably means something very different!

### empty array
Imagine a schema 
```json
[{"foo": ["bar"]}]
``` 
If `foo` was absent and dehydrated using an empty array then we'd have 
```json
[[]]
```
Which would rehydrate to 
```json
{"foo": {}}
```
because we can't differentiate between an absent field and an empty object value.

### "magic" value
A specific string like `"json-anon:empty-field"` could be used as a placeholder for absent fields and then removed on rehydration. However that string is huge.   
We could also use an obscure unicode character like `"ᚘ"` and hope that no one ever needs a string with just that character. But that feels a bit hacky and still uses 4 bytes.
### don't require dehydrated objects to be valid JSON
Allowing arrays to have empty elements would solve our problem. But this would require custom parsers and would greatly harm adoptability.
### empty object
An empty object seems like our best option. It's only 2 bytes, very rare that any use case would want a completely empty object as a field value, and it can be differentiated from a dehydrated empty object.