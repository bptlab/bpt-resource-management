# Manage Entries #

ResourceLifeCycle describes the general operations that shall be carried out by resource providers and moderators of the platform to create, update, and manage the life cycle of such a resource.

**Matthias:** From my perspective, these operations can be included in the main functions [#List\_Entries](#List_Entries.md), [#Search\_Entries](#Search_Entries.md), and [#Show\_Detail](#Show_Detail.md) for authorized providers (they can manage their own resources only) and moderators (they can manage all resources), according to their respective [rights](ResourceLifeCycle.md).

For each resource in a list view ([#List\_Entries](#List_Entries.md), [#Search\_Entries](#Search_Entries.md)) links to atomic updates of the state of the resource shall be provided (edit, un-/publish, check) that are updated via PUT requests (REST).

All possible operations shall be available to authorized providers and moderators in the [#Show\_Detail](#Show_Detail.md) dialogue. This includes delete and reject that are not to be shown in list views.

# List Entries #

  * list all entries, sort by different attributes
  * requires list template

# Search Entries #

  * search and filter entries by different attributes
  * provides a list of results
  * uses list template from [#List\_Entries](#List_Entries.md)

# Show Detail #

  * provides detailed information for each resource
  * requires separate template