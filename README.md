# json-anon

Given some schema for an object, you could "anonymize it" into an array by removing the field names. On the other end, you could de-anonymize an array into an object given a schema. 
ie.
{ version: 00, trace-id: 4bf92f3577b34da6a3ce929d0e0e4736, parent-id: 00f067aa0ba902b7, trace-flags: 01 }
-
[version, trace-id, parent-id, trace-flags]
=
[00, 4bf92f3577b34da6a3ce929d0e0e4736, 00f067aa0ba902b7, 01]
+
[version, trace-id, parent-id, trace-flags]
=
{ version: 00, trace-id: 4bf92f3577b34da6a3ce929d0e0e4736, parent-id: 00f067aa0ba902b7, trace-flags: 01 }
