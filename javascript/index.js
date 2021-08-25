function expandSchema(schema) {
	if (typeof schema === "string") {
		return { type: "reference", schema: schema.slice(5) };
	} else if (Array.isArray(schema)) {
		let expandedFields = schema.map(e => {
			if (typeof e === "object" && !(e === null)) {
				let [field, value] = Object.entries(e)[0];
				let ret = {};
				ret[field] = expandSchema(value);
				return ret;
			} else {
				return e;
			}
		});
		return { type: "object", schema: expandedFields };
	} else if (schema.type === "array") {
		return { type: "array", schema: expandSchema(schema.schema) };
	} else {
		return schema;
	}
}

function dehydrate(schemas, schema, object) {
	//console.log(JSON.stringify(schema) + ": " + JSON.stringify(object));
	schema = expandSchema(schema);
	switch (schema.type) {
		case "array":
			return object.map(e => dehydrate(schemas, schema.schema, e));
		case "object":
			return schema.schema.map(s => {
				if (s === null) {
					return 0;
				} else if (typeof s === "string") {
					if (object.hasOwnProperty(s)) {
						return object[s];
					} else {
						return {};
					}
				} else if (typeof s === "object") {
					let [field, value] = Object.entries(s)[0];
					if (object.hasOwnProperty(field)) {
						return dehydrate(schemas, value, object[field]);
					} else {
						return {};
					}
				}
			});
		case "reference":
			return dehydrate(schemas, schemas[schema.schema], object);
	}
}

function hydrate(schemas, schema, object) {
	schema = expandSchema(schema);
	if (JSON.stringify(object) != "{}") {
		switch (schema.type) {
			case "array":
				return object.map(e => hydrate(schemas, schema.schema, e));
			case "object":
				var i = 0;
				return Object.fromEntries(schema.schema.flatMap(s => {
					if (s === null) {
						i++;
						return [];
					} else {
						switch (typeof s) {
							case "string":
								if (JSON.stringify(object[i]) == "{}") {
									i++;
									return [];
								} else {
									return [[s, object[i++]]];
								}
							case "object":
								let [field, value] = Object.entries(s)[0];
								return [[field, hydrate(schemas, value, object[i++])]];
						}
					}
				}));

			case "reference":
				return hydrate(schemas, schemas[schema.schema], object);
		}
	}
}
