# Meta Model #

Each resource has a **unique id**, and a set of **attributes**. The set of attributes can be arbitrary.

Each attribute has the following **properties**

| data type | the actual data type |
|:----------|:---------------------|
| private   | is the attribute hidden from anybody but the provider of the resource and the moderators |
| searchable | shall the attribute be considered for search |
| required  | is the attribute required for input |
| default   | a default value for this attribute, may be empty |

## Tags ##

Tags must receive special care: While their name may change over time, the links between them must be kept. E.g., if a resource A is tagged with b,c, and d today and b is renamed to b' than A must be tagged with b',c, and d afterwards.


### bpm-conference ###

| **attribute**   | **data type** | **required**  | **private** | **searchable**| default | comment |
|:----------------|:--------------|:--------------|:------------|:--------------|:--------|:--------|
| name            | string        | yes           | no          | yes           |         |         |
| description     | rich text     | yes           | no          | yes           |         |         |
| provider        | string        | yes           | no          | yes           |         |         |
| download        | URL           | yes           | no          | yes           |         |         |
| documentation   | URL           | yes           | no          | yes           |         |         |
| screencast      | URL           | yes           | no          | yes           |         |         |
| availability    | multi choice + free text'**'**| yes           | no          | no            |         | open source, freeware, shareware, free for academics, commercial, other'**'**|
| model types     | multi choice + free text'**'**| yes           | no          | no            |         | BPMN, EPC, Petri net, UML Activity Diagram, Workflow Net, YAWL, BPEL, other'**'**|
| platform        | multi choice + free text'**'**| yes           | no          | no            |         | SaaS, Windows, Linux, Mac OSX, Android, iOS, other'**'**|
| supported functionality | multi choice + free text'**'**| yes           | no          | no            | [#Tags](#Tags.md) | graphical model editor, model repository, verification of model properties, enactment, runtime adaptation, process discovery based on event data, conformance checking based on event data, other'|
| contact name    | string        | yes           | no          | yes           |         |         |
| contact mail    | emailaddress  | yes           | yes         | no            |         |         |
| lastupdate      | date          | yes           | no          | no            | set automatically on create/update |         |
