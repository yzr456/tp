---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# Zenith Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores a centralized view of all scheduled sessions (which are managed by a `WeeklySessions` object). See the [WeeklySessions Component](#weeklysessions-component) section for implementation details.
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user's preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
    * Pros: Easy to implement.
    * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
    * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
    * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_

### WeeklySessions Component

The `WeeklySessions` component provides a centralized, time-sorted view of all scheduled sessions across all persons in the address book. It augments the `AddressBook` with efficient time-based query capabilities.

#### Purpose
- Maintains a global schedule view of all sessions
- Enables efficient overlap detection
- Supports free time gap-finding algorithms
- Provides sorted session iteration

#### Architecture

`WeeklySessions` implements a dual-storage pattern within `AddressBook`:

1. **Person-centric view**: Sessions stored as `SessionTag` objects within each `Person`'s tags collection
2. **Time-centric view**: Sessions stored in a sorted `TreeSet<Session>` within `WeeklySessions`


#### Key Data Structures

**TreeSet Ordering**: Sessions are automatically sorted using `Session.compareTo()`:
1. By `DayOfWeek` (MON → SUN)
2. Then by `startTime`
3. Then by `endTime`

This ordering enables efficient linear scanning for free time queries.

**Reference Counting**: The `sessionCounts` map tracks how many persons share each session. A session is only removed from the TreeSet when its count reaches zero, preventing premature deletion when multiple students attend the same session time.

#### Core Operations

**Adding Sessions** `add(Session session)`:
- If session already exists: increment reference count
- If new session: add to TreeSet and initialize count to 1

**Removing Sessions** `remove(Session session)`:
- Decrements reference count
- Only removes from TreeSet when count reaches 0

**Overlap Detection** `getOverlap(Session sessionToCheck)`:
- Returns `Optional<Session>` of first overlapping session

**Free Time Search** `getEarliestFreeTime(int duration)`:
- Linear scan from Monday 08:00 to Sunday 22:00
- Finds first gap that fits requested duration
- Returns formatted time string or "No free time available"

**Algorithm Details:**

The algorithm searches for the earliest available time slot by performing a chronological scan through the week:

1. **Search Space Definition:**
   - Days: Monday through Sunday (in order)
   - Time window per day: 08:00 to 22:00 (representing typical working hours)
   - Minimum duration: Specified by user (in minutes)

2. **Day-by-Day Iteration:**
   - For each day of the week (starting from Monday):
     - Initialize current time to 08:00 (earliest possible start time)
     - Filter all sessions for the current day from the sorted TreeSet
     - Sessions are already sorted by start time due to TreeSet ordering

3. **Gap Detection Within Each Day:**
   - For each session on the current day:
     - Calculate gap duration: time between current time and session start time
     - If gap duration ≥ requested duration:
       - A suitable slot is found → return formatted result (e.g., "MON 08:00")
     - Otherwise:
       - Move current time to session end time (skip past this occupied slot)
       - Continue to next session

4. **Final Gap Check:**
   - After processing all sessions for the day:
     - Calculate gap duration: time between current time and 22:00 (end of working hours)
     - If gap duration ≥ requested duration:
       - A suitable slot is found → return formatted result
     - Otherwise:
       - Move to next day and repeat from step 2

5. **No Availability:**
   - If all days are scanned without finding a suitable gap:
     - Return "No free time available for the specified duration."

_Refer to the user guide for example usages_


#### Data Persistence

**Storage Strategy**: Only `Person` objects (with their `SessionTag`s) are serialized to JSON. The `WeeklySessions` TreeSet is not directly persisted.

**Reconstruction on Load** :
When deserializing, `WeeklySessions` is rebuilt from person tags:

This ensures:
- Single source of truth (Person tags in JSON)
- Automatic reference counting during reconstruction
- No data duplication in storage

#### Design Rationale

**Why dual storage?**
- **Person tags**: Efficient person-to-session lookups ("Which sessions does Alice have?")
- **TreeSet**: Efficient time-based queries ("What's the earliest free slot?")

**Why reference counting?**
Multiple students can share the same session time (e.g., group tutoring). Reference counting prevents deletion conflicts.

### Free Feature
The `free` feature is facilitated by `WeeklySessions`. It augments `Addressbook` by providing a centralised time sorted view of all scheduled sessions across all persons. It is stored internally as `weeklySessions` which gives `AddressBooks` two complementary views of session data

The functionality of `WeeklySessions` are exposed to `Addressbook` and the `Model` interface with similar function names:
* `WeeklySessions#add` -> `AddressBook/Model#addSession`
* `WeeklySessions#remove` -> `AddressBook/Model#removeSession`
* `WeeklySessions#set` -> `AddressBook/Model#setWeeklySessions`
* `WeeklySessions#getOverlap` -> `AddressBook/Model#getOverlappingSessions`
* `WeeklySessions#getEarliestFreeTime` -> no change

The sequence diagram for free is similar to `delete` sequence diagram we see in [Logic component](#logic-component), with some key differences:
* Command will be `free DURATION` instead of `delete INDEX`
* Command and its parser will be `Free` instead of `Delete`
* `FreeCommand` will call `Model#getEarliestFreeTime(1)`

The free operation will go through the `Model` component as such:
<puml src="diagrams/FreeSequenceDiagram-Model.puml" />




--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* tech savvy private tutor
* manages 10-50+ students across different education levels (primary to university)
* prefers typing commands over mouse clicks for speed and efficiency
* needs quick access to information during back-to-back sessions (no time for slow navigation)
* juggles students of different levels, making it hard to provide personalized learning experiences
* struggles to manage students' conflicting schedules and optimize their own time
* faces long travel times between sessions due to poor planning around student locations and timetables
* has student and parent details scattered across phone contacts, WhatsApp chats, and loose business cards
* values data privacy and prefers local storage over cloud-based solutions

**Value proposition**: A lightning-fast CLI address book designed for tech-savvy private tutors managing 10-50+ students. Centralizes scattered student information, tracks subjects and session schedules, and enables instant filtering by location, time, or student details—all through efficient keyboard commands that outpace traditional apps. Spend less time organizing, more time teaching.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                    | I want to …​                 | So that I can…​                                                        |
|----------|--------------------------------------------|------------------------------|------------------------------------------------------------------------|
| `* * *`  | private tutor                              | add a tutee contact record with name, contact, location, age/level | quickly access essential details |
| `* * *`  | private tutor                              | remove a tutee contact       | have an updated contact list                                           |
| `* * *`  | private tutor                              | edit student details         | keep their records up to date                                          |
| `* * *`  | private tutor with many students           | easily look for students by name | quickly access their information during busy tutoring days         |
| `* * *`  | fast typing private tutor                  | perform tasks using short efficient commands | save time                                              |
| `* * *`  | first time user                            | have a help command          | know how to use the application                                        |
| `* * *`  | private tutor                              | store multiple contact methods per person (phone, email, WhatsApp, etc.) | have backup communication options |
| `* * *`  | private tutor                              | locally store all student-related and personal data | never lose important information                |
| `* *`    | private tutor                              | view a calendar of upcoming sessions | manage my time before those sessions                           |
| `* *`    | private tutor                              | group students by classes    | organise my addressbook                                                |
| `* *`    | private tutor                              | receive reminders for lessons | don't miss any sessions                                               |
| `* *`    | private tutor who earns money              | keep track of payments I received |  streamline my finances                                           |
| `* *`    | user                                       | delete all my personal data (non-contact related information) | remove all personal information       |
| `* *`    | private tutor                              | track test scores            | monitor improvement over time                                          |
| `* *`    | private tutor                              | record the topics each student has covered | identify gaps in knowledge                               |
| `* *`    | effective private tutor                    | mark areas where a student struggles | focus those in future lessons.                                 |
| `* *`    | effective private tutor                    | highlight a student’s strong skills | build on them                                                   |
| `* *`    | private tutor                              | record different rates for different students or classes | manage varying fees more easily            |
| `* *`    | private tutor                              | log actual hours worked per student |  accurately bill and track my time investment                   |
| `* *`    | beginner user                              | have a demo/tutorial         | have an intuitive way to learn the application                         |
| `*`      | private tutor                              | assign projects to each student |  tailor their learning paths                                        |
| `*`      | private tutor                              | update project completion status |  track project progress                                            |
| `*`      | time-efficient private tutor               | filter students by location  |  arrange back-to-back F2F lessons efficiently                          |
| `*`      | online private tutor                       | track time zones for international students |  schedule sessions at reasonable hours for everyone     |

*{More to be added}*

### Use cases

(For all use cases below, the **System** is the `Zenith` and the **Actor** is the `Tutor`, unless specified otherwise)

**Use case: UC01 – Add Contact**

**Guarantees**
- A new contact is stored only if all fields are valid and the phone number is unique.
- On success, the updated list will reflect the new contact.

**MSS**
1. Tutor provides a contact with its details to add.
2. Zenith validates the details.
3. Zenith adds the contact to the list and shows the new contact.
4. Includes: UC11 Autosave.
Use case ends.

**Extensions**
* 2a. Zenith detects that any detail is invalid.
    * 2a1. Zenith shows the specific error (e.g., NameError, StudyYearError, PhoneError, AddressError, DuplicateError).
      Use case ends.
* 2b. Zenith detects that the contact already exists.
    * 2b1. Zenith shows DuplicateError.
      Use case ends.


**Use case: UC02 – Delete Contact**

**Preconditions**
- At least one contact is listed.

**Guarantees**
- The specified contact is removed only if a valid index is given.
- On success, the updated list will no longer show the contact.

**MSS**
1. Tutor provides the index of the contact to be deleted.
2. Zenith validates the index.
3. Zenith removes the contact from the list and shows the deleted contact.
4. Includes: UC11 Autosave.
Use case ends.

**Extensions**
* 2a. Zenith detects that the index is invalid or missing.
    * 2a1. Zenith shows InvalidIndexError or ArgumentError.
      Use case ends.


**Use case: UC03 – Edit Contact**

**Preconditions**
- The target contact exists.

**Guarantees**
- Only provided fields are updated; phone numbers in the list remain unique.
- On success, Zenith shows the updated contact.

**MSS**
1. Tutor provides the index and new values of the details of the contact to be edited.
2. Zenith validates the index and any provided values.
3. Zenith updates the contact and shows the updated contact.
4. Includes: UC11 Autosave.
Use case ends.

**Extensions**
* 2a. Zenith detects that the index is invalid or missing.
    * 2a1. Zenith shows InvalidIndexError / ArgumentError.
      Use case ends.
* 2b. Zenith detects that any provided field is invalid.
    * 2b1. Zenith shows the specific error (e.g., NameError, StudyYearError, PhoneError, AddressError, DuplicateError).
      Use case ends.


**Use case: UC04 – Edit Session Timing for a Contact**

**Preconditions**
- The contact and the target session exist.

**Guarantees**
- The session time is updated only if the new slot is valid and non-overlapping.
- On success, Zenith shows the contact with updated sessions.

**MSS**
1. Tutor provides the contact index, session timing details to be edited.
2. Zenith validates indices and time values.
3. Zenith checks for overlaps with that contact’s existing sessions.
4. Zenith updates the session and shows the updated contact.
5. Includes: UC11 Autosave.
Use case ends.

**Extensions**
* 2a. Zenith detects that the index is missing or invalid.
    * 2a1. Zenith shows InvalidIndexError / InvalidSessionIndexError.
      Use case ends.
* 2b. Zenith detects that the day is invalid.
    *2 b1. Zenith shows InvalidDayError.
      Use case ends.
* 2c. Zenith detects that the time format or range is invalid.
    * 2c1. Zenith shows TimeFormatError / TimeRangeError.
      Use case ends.
* 3a. Zenith detects that the updated session overlaps an existing session
    * 3a1. Zenith shows SessionConflictError.
      Use case ends.


**Use case: UC05 – Add Session Timing to a Contact**

**Preconditions**
- The contact exists.

**Guarantees**
- A session is added only if the slot is valid and non-overlapping.
- On success, Zenith shows the contact with the new session.

**MSS**
1. Tutor provides the contact index and session timing details to be added.
2. Zenith validates indices and time values.
3. Zenith checks for overlaps with that contact’s existing sessions.
4. Zenith adds the session and shows the updated contact.
5. Includes: UC11 Autosave.
Use case ends.

**Extensions**
Same as UC04


**Use case: UC06 – Add Subject Tag to a Contact**

**Preconditions**
- The contact exists.

**Guarantees**
- A subject tag is added only if the subject code is valid and not already present.
- On success, Zenith shows the contact with the new tag.

**MSS**
1. Tutor provides the contact index and the subject code for the tag to be added.
2. Zenith validates the index and subject code.
3. Zenith adds the tag and shows the updated contact.
4. Includes: UC11 Autosave.
Use case ends.

**Extensions**
* 2a. Zenith detects that the index is invalid or missing
    * 2a1. Zenith shows InvalidIndexError / ArgumentError.
      Use case ends.
* 2b. Zenith detects that the subject code is missing or invalid
    * 2b1. Zenith shows InvalidSubjectError.
      Use case ends.
* 2c. Zenith detects that the subject tag is already on the contact
    * 2c1. Zenith shows DuplicateSubjectError.
      Use case ends.


**Use case: UC07 – Find Contacts**

**Guarantees**
- Zenith shows a filtered contact list based on the keywords; no data is modified.

**MSS**
1. Tutor provides the keywords to find contacts by.
2. Zenith validates that at least one non-empty keyword is provided.
3. Zenith filters the list and shows the results.
Use case ends.

**Extensions**
* 2a. Zenith detects that no valid keyword is provided
    * 2a1. Zenith shows ArgumentError.
      Use case ends.


**Use case: UC08 – List Contacts**

**Guarantees**
- Zenith shows the full contact list; no data is modified.

**MSS**
1. Tutor requests to list contacts.
2. Zenith shows all contacts.
Use case ends.


**Use case: UC09 – Help**

**Guarantees**
- Zenith shows the available commands with brief descriptions.

**MSS**
1. Tutor requests help.
2. Zenith displays the available commands.
Use case ends.


**Use case: UC10 – Exit**

**Guarantees**
- Zenith exits gracefully

**MSS**
1. Tutor requests to exit.
2. Zenith terminates.
Use case ends.


**Use case: UC11 – Autosave**

**Actor: File System**

**Preconditions**
- A data-modifying command has just succeeded.

**Guarantees**
- Latest state is written to local storage
- On failure Zenith warns with an error message

**MSS**
1. Zenith writes the updated data to the save file.
Use case ends.

**Extensions**
* 1a. Zenith detects write failure due to permissions/disk
    * 1a1. Zenith shows FileAccessError or DiskSpaceError.
      Use case ends.
* 1b. Zenith detects that the existing save file is corrupted
    * 1b1. Zenith shows FileCorruptionError and attempts recovery/backup; on failure show BackupCreationError.
	  Use case ends.

*{More to be added}*

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Autosave functionality must always preserve user data immediately after every data-changing command (add, delete, edit, session/subject changes) with no perceptible delay.
5.  Application startup, shutdown, and command responses for core actions (add, edit, delete, addsession, addsubject) should complete in under 1 second for databases with up to 1000 students on consumer-grade hardware.
6.  The application shall recover gracefully from partial file corruption, access errors, or disk space insufficiency, displaying clear error messages, attempting restoration from the most recent backup, and never crashing silently or destroying unsaved user data.
7.  All error and warning messages must be concise, clear, and non-ambiguous, allowing users to recover independently without technical support.
8.  The _core functions_ of the application must work completely offline with no network dependencies, ensuring reliability regardless of internet connectivity.

*{More to be added}*

### Glossary

* **CLI (Command Line Interface)**: Text-based interface where users interact with the application by typing commands rather than clicking buttons or menus.
* **Contact**: A student record in zenith containing personal details, subjects, sessions, and other tutoring-related information.
* **Core Functions**: Add, Edit, Delete, add Session, add Subject
* **HHmm format**: 24-hour time notation using 4 digits (e.g., 0900 for 9:00 AM, 1530 for 3:30 PM).
* **Index**: The position number of a student contact as displayed in the current list (e.g., 1, 2, 3).
* **JC1/JC2**: Junior College Year 1 and Year 2 - pre-university education levels in Singapore (usually ages 17-18).
* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **SEC1 - SEC5**: Secondary school years 1-5 in Singapore education system (usually ages 13-17).

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   2. CD into the JAR file directory and run java -jar zenith.jar Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum
   3. (Alternative) Double-click the JAR file Expected: Similar behaviour to running it through command line

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by running it through command line.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Adding a person
1. Adding a person
   1. Prerequisites: At the start of every new test(_new number_) case run `Clear` to ensure that the address book is empty. 
   2. Test case 1(a): `add n/bill s/SEC2 p/83451234 e/bill@gmail.com a/yishun street 10`<br> Expected: New contact named bill is added to the list
   3. Test case 1(b): `add n/bill s/SEC3 p/91203442 e/billbi@gmail.com a/yishun street 11`<br> Expected: New contact named bill is added to the list
   4. Test case 2(a): `add n/bill s/SEC2 p/83451234 e/bill@gmail.com a/yishun street 10`<br> Expected: New contact named bill is added to the list
   5. Test case 2(b): `add n/bill s/PRI5 p/83451234 e/billbo@gmail.com a/yishun street 12` <br> Expected: No new person is added to the list due to duplicate contacts. Error details shown in the status message.
   6. Test case 3: `add n/bill s/PRI4 e/bill@gmail.com a/yishun street 10` <br> Expected: No new person is added to the list due to missing parameter. Error details shown in status message

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
