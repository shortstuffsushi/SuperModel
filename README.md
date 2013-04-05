SuperModel
==========

The SuperModel project allows for the modeling of POJOs (Plain Old Java Objects) via a GUI based on that of the Xcode Data Modeling tool.

The tool allows the Creation of Entities, which represent the Java class, and the database Table.

These Entities have Attributes (object properties, table columns), and Relationships (references to other objects, foreign keys).

After creating the Entities, the tool can do several more useful things:
-- It can generate the Java code representing the Entities, including their basic Attributes, Relationships, get/set methods, and static "getByXXX" methods.
-- It can generate the database tables representing the Entities, including their basic properties and extra foriegn key fields for Relationships.

The project is currently being used in correlation with the [SQLiteORM](https://github.com/njkremer/SqliteORM), but will eventually also support other ORMs like Hibernate.
