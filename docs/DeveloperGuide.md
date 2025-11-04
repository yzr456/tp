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

Zenith is built on top of code from [AB-3](https://github.com/se-edu/addressbook-level3).
Some portions of the codebase were generated with assistance from JetBrains AI, Claude AI, including:

* Generation of test cases and error handling scenarios
* Code cleanup and refactoring suggestions
* Documentation formatting and style consistency
* Helper methods for certain operations

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

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S1-CS2103-F12-4/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2526S1-CS2103-F12-4/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
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

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S1-CS2103-F12-4/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `PersonCard`, `DetailedView`, `StatusBarFooter`, `HelpWindow` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S1-CS2103-F12-4/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S1-CS2103-F12-4/tp/tree/master/src/main/resources/view/MainWindow.fxml)

**Main UI Structure:**

* **Header**: Application logo and Help button
* **CommandBox**: Text field for command input with visual error feedback
* **ResultDisplay**: Read-only text area showing command execution results
* **Split Pane** (30% / 70% fixed division):
  * **PersonListPanel** (left): Scrollable list of contacts, each rendered as a `PersonCard` showing index, name, study year, contact details, session tags and subject tags
  * **DetailedView** (right): Comprehensive view of selected contact including all fields, payment status, color-coded subject tags, and color-coded session tags
* **StatusBarFooter**: Displays save file location
* **HelpWindow**: Separate modal dialog with command usage and user guide link

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data via `ObservableList<Person>` so that the UI can be updated automatically.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` objects residing in the `Model`.
* implements keyboard-first interaction:
  * Typing alphanumeric keys anywhere focuses `CommandBox` and inserts the character
  * Typing `ctrl` anywhere focuses `CommandBox`
  * UP/DOWN arrow keys navigate the address book
  * F1 opens `HelpWindow`
* automatically updates `DetailedView` when a contact is selected (via click or keyboard navigation).

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S1-CS2103-F12-4/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for Parser classes should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
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
**API** : [`Model.java`](https://github.com/AY2526S1-CS2103-F12-4/tp/blob/master/src/main/java/seedu/address/model/Model.java)

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

**API** : [`Storage.java`](https://github.com/AY2526S1-CS2103-F12-4/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

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

* Tech savvy private tutor
* Manages 10-50+ students in Singapore across different education levels (primary to university)
* Prefers typing commands over mouse clicks for speed and efficiency
* Needs quick access to information during back-to-back sessions (no time for slow navigation)
* Struggles to manage students' conflicting schedules and optimize their own time
* Has student details scattered across phone contacts, WhatsApp chats, and loose notes
* Values data privacy and prefers local storage over cloud-based solutions

**Value proposition**: This product is for a tech-savvy private tutor teaching students in Singapore, who prefers the use of CLI over GUI for its efficiency and minimalism. It simplifies tutoring workflow with a command-line address book that centralises student details, payments, and optimizes scheduling, all designed to enhance personalised tutoring through quick, efficient access and management.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                          | I want to …​                                                       | So that I can…​                                            |
|----------|----------------------------------|--------------------------------------------------------------------|------------------------------------------------------------|
| `* * *`  | private tutor                    | add a tutee contact record with name, contact, location, age/level | quickly access essential details                           |
| `* * *`  | private tutor                    | remove a tutee contact                                             | have an updated address book                               |
| `* * *`  | private tutor                    | edit student details                                               | keep their records up to date                              |
| `* * *`  | private tutor with many students | easily look for students by name                                   | quickly access their information during busy tutoring days |
| `* * *`  | fast typing private tutor        | perform tasks using short efficient commands                       | save time                                                  |
| `* * *`  | first time user                  | have a help command                                                | know how to use the application                            |
| `* * *`  | private tutor                    | store multiple contact methods per person (phone, email, etc.)     | have backup communication options                          |
| `* * *`  | private tutor                    | locally store all student-related and personal data                | never lose important information                           |
| `* * *`  | private tutor                    | add tutoring sessions for each student                             | track when I meet with each student                        |
| `* * *`  | private tutor                    | edit tutoring sessions                                             | update my schedule when times change                       |
| `* * *`  | private tutor                    | tag students with subjects they are taking                         | quickly see what I am teaching each student                |
| `* * *`  | busy private tutor               | find the earliest free time slot in my schedule                    | efficiently schedule new students                          |
| `* * *`  | private tutor                    | view all my contacts in a list                                     | see my entire student roster at once                       |
| `* * *`  | private tutor                    | clear all contacts                                                 | start fresh when needed                                    |
| `* *`    | private tutor who earns money    | keep track of payments I received                                  | streamline my finances                                     |
| `* *`    | private tutor                    | set payment status for each student                                | track who has paid and who has not                         |
| `* *`    | private tutor                    | set billing cycle start days                                       | align payment tracking with my billing schedule            |
| `* *`    | private tutor                    | see how many days a payment is overdue                             | follow up with late payments promptly                      |
| `* *`    | private tutor                    | view a calendar of upcoming sessions                               | manage my time before those sessions                       |
| `* *`    | private tutor                    | group students by classes                                          | organise my list of contacts                               |
| `* *`    | private tutor                    | receive reminders for lessons                                      | do not miss any sessions                                   |
| `* *`    | private tutor                    | track test scores                                                  | monitor improvement over time                              |
| `* *`    | private tutor                    | record the topics each student has covered                         | identify gaps in knowledge                                 |
| `* *`    | effective private tutor          | mark areas where a student struggles                               | focus those in future lessons.                             |
| `* *`    | effective private tutor          | highlight a student’s strong skills                                | build on them                                              |
| `* *`    | private tutor                    | record different rates for different students or classes           | manage varying fees more easily                            |
| `* *`    | private tutor                    | log actual hours worked per student                                | accurately bill and track my time investment               |
| `* *`    | beginner user                    | have a demo/tutorial                                               | have an intuitive way to learn the application             |
| `*`      | private tutor                    | assign projects to each student                                    | tailor their learning paths                                |
| `*`      | private tutor                    | update project completion status                                   | track project progress                                     |
| `*`      | time-efficient private tutor     | filter students by location                                        | arrange back-to-back F2F lessons efficiently               |

### Use cases

(For all use cases below, the **System** is the `Zenith` and the **Actor** is the `Tutor`, unless specified otherwise)

**Use case: UC01 – Add Contact**

**Guarantees**
- A new contact is stored only if all fields are valid and neither phone nor email duplicates exist.
- On success, the updated list will reflect the new contact.

**MSS**
1. Tutor provides contact details including name, study year, phone, email, and address.
2. Zenith validates all details.
3. Zenith checks for duplicate phone or email in existing contacts.
4. Zenith adds the contact to the list and displays the new contact.
5. Includes: UC13 Autosave.
Use case ends.

**Extensions**
* 2a. Zenith detects invalid or missing details.
    * 2a1. Zenith shows an error message indicating which field is invalid.
      Use case ends.
* 3a. Zenith detects that the phone number already exists in another contact.
    * 3a1. Zenith shows an error message indicating duplicate phone number.
      Use case ends.
* 3b. Zenith detects that the email already exists in another contact.
    * 3b1. Zenith shows an error message indicating duplicate email.
      Use case ends.
* 3c. Zenith detects that both phone number and email already exist in another contact.
    * 3c1. Zenith shows an error message indicating duplicate phone and email.
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
3. Zenith removes the contact from the list and displays the deleted contact.
4. Includes: UC13 Autosave.
Use case ends.

**Extensions**
* 2a. Zenith detects that the index is invalid or missing.
    * 2a1. Zenith shows an error message indicating the index issue.
      Use case ends.


**Use case: UC03 – Edit Contact**

**Preconditions**
- The target contact exists.

**Guarantees**
- Only provided fields are updated; phone numbers and emails in the list remain unique.
- On success, Zenith displays the updated contact.

**MSS**
1. Tutor enters the edit command with the contact index and at least one field to update
   (name, study year, phone, email, address, subjects, or sessions).
2. Zenith validates the index and provided values.
3. Zenith checks if the new phone or email conflicts with other contacts.
4. Zenith updates the contact and displays the updated contact.
5. Includes: UC13 Autosave.
Use case ends.

**Extensions**
* 2a. Zenith detects that the index is invalid or missing.
    * 2a1. Zenith shows an error message indicating the index issue.
      Use case ends.
* 2b. Zenith detects that any provided field value is invalid.
    * 2b1. Zenith shows an error message indicating which field is invalid.
      Use case ends.
* 3a. Zenith detects that the new phone number already exists in another contact.
    * 3a1. Zenith shows an error message indicating duplicate phone number.
      Use case ends.
* 3b. Zenith detects that the new email already exists in another contact.
    * 3b1. Zenith shows an error message indicating duplicate email.
      Use case ends.
* 3c. Zenith detects that both the new phone number and email already exist in another contact.
    * 3c1. Zenith shows an error message indicating duplicate phone and email.
      Use case ends.


**Use case: UC04 – Add Session to Contact**

**Preconditions**
- The contact exists.

**Guarantees**
- A session is added only if the slot is valid and non-overlapping within that contact's schedule.
- On success, Zenith displays the contact with the new session.

**MSS**
1. Tutor provides the contact index and session timing (day, start time, end time).
2. Zenith validates the index, day, and time values.
3. Zenith checks for overlaps with the contact's existing sessions.
4. Zenith adds the session to the contact and displays the updated contact.
5. Includes: UC13 Autosave.
Use case ends.

**Extensions**
* 2a. Zenith detects that the index is invalid or missing.
    * 2a1. Zenith shows an error message indicating the index issue.
      Use case ends.
* 2b. Zenith detects that the day is invalid.
    * 2b1. Zenith shows an error message indicating invalid day.
      Use case ends.
* 2c. Zenith detects that the time format is invalid or the end time is before start time.
    * 2c1. Zenith shows an error message indicating the time issue.
      Use case ends.
* 3a. Zenith detects that the new session overlaps with an existing session.
    * 3a1. Zenith shows an error message indicating session conflict.
      Use case ends.


**Use case: UC05 – Add Subject Tag to Contact**

**Preconditions**
- The contact exists.

**Guarantees**
- A subject tag is added only if the subject code is valid and not already present.
- On success, Zenith displays the contact with the new tag.

**MSS**
1. Tutor provides the contact index and one or more subject codes.
2. Zenith validates the index and subject codes.
3. Zenith checks that no subject is specified more than once in the command.
4. Zenith checks that the subject tags do not already exist for the contact.
5. Zenith adds the subject tags to the contact and displays the updated contact.
6. Includes: UC13 Autosave.
Use case ends.

**Extensions**
* 2a. Zenith detects that the index is invalid or missing.
    * 2a1. Zenith shows an error message indicating the index issue.
      Use case ends.
* 2b. Zenith detects that a subject code is invalid.
    * 2b1. Zenith shows an error message indicating invalid subject code.
      Use case ends.
* 3a. Zenith detects that the same subject is specified multiple times in the command.
    * 3a1. Zenith shows an error message indicating duplicate subject in command.
      Use case ends.
* 4a. Zenith detects that one or more subject tags already exist for the contact.
    * 4a1. Zenith shows an error message indicating which subjects are already assigned.
      Use case ends.


**Use case: UC06 – Set Payment Status**

**Preconditions**
- The target contact exists.

**Guarantees**
- Payment status is updated only if the index is valid and status is PENDING, PAID, or OVERDUE.
- Billing start day, if provided, must be between 1-31.
- On success, Zenith displays the updated contact with new payment information.

**MSS**
1. Tutor provides the contact index and payment status.
2. Zenith validates the index and payment status.
3. Zenith updates the contact's payment status and displays the updated contact.
4. Includes: UC13 Autosave.
Use case ends.

**Extensions**
* 1a. Tutor provides optional billing start day.
    * 1a1. Zenith validates billing start day is between 1-31.
    * 1a2. Zenith updates payment status with the specified billing start day.
      Use case continues from step 3.
* 2a. Zenith detects that the index is invalid or missing.
    * 2a1. Zenith shows an error message indicating the index issue.
      Use case ends.
* 2b. Zenith detects that the payment status is invalid.
    * 2b1. Zenith shows an error message indicating invalid payment status.
      Use case ends.
* 2c. Zenith detects that the billing start day is not between 1-31.
    * 2c1. Zenith shows an error message indicating invalid billing day.
      Use case ends.


**Use case: UC07 – Find Earliest Free Time**

**Guarantees**
- Zenith shows the earliest available time slot across all contacts' schedules that fits the requested duration.
- No data is modified.

**MSS**
1. Tutor provides the duration in minutes for the desired time slot.
2. Zenith validates the duration is a positive integer.
3. Zenith searches through the weekly schedule to find the earliest free time slot.
4. Zenith displays the earliest available time or indicates no free time is available.
Use case ends.

**Extensions**
* 2a. Zenith detects that the duration is missing or invalid.
    * 2a1. Zenith shows an error message indicating invalid duration.
      Use case ends.
* 3a. Zenith finds no available time slot that fits the requested duration.
    * 3a1. Zenith shows a message indicating no free time is available.
      Use case ends.


**Use case: UC08 – Find Contacts**

**Guarantees**
- Zenith shows a filtered address book based on the keywords; no data is modified.

**MSS**
1. Tutor provides one or more keywords to search for.
2. Zenith validates that at least one non-empty keyword is provided.
3. Zenith filters the address book and displays matching results.
Use case ends.

**Extensions**
* 2a. Zenith detects that no valid keyword is provided.
    * 2a1. Zenith shows an error message indicating missing keywords.
      Use case ends.


**Use case: UC09 – List Contacts**

**Guarantees**
- Zenith shows the full address book; no data is modified.

**MSS**
1. Tutor requests to list all contacts.
2. Zenith displays all contacts.
Use case ends.


**Use case: UC10 – Clear All Contacts**

**Guarantees**
- All contacts are removed from the address book only if the tutor confirms the action.
- On success, Zenith displays an empty address book.
- On cancellation, all contacts remain unchanged.

**MSS**
1. Tutor requests to clear all contacts.
2. Zenith requests confirmation from the tutor.
3. Tutor confirms the action.
4. Zenith removes all contacts from the address book.
5. Zenith displays an empty list with a success message.
6. Includes: UC13 Autosave.
Use case ends.

**Extensions**
* 3a. Tutor cancels the action.
    * 3a1. Zenith displays a cancellation message.
    * 3a2. Zenith retains all contacts in the list.
      Use case ends.
* 3b. Tutor provides invalid input.
    * 3b1. Zenith displays an error message requesting valid input (y/n).
    * 3b2. Zenith does not remove any contacts.
      Use case ends.


**Use case: UC11 – Help**

**Guarantees**
- Zenith shows the available commands with brief descriptions.

**MSS**
1. Tutor requests help.
2. Zenith displays the available commands and usage guide.
Use case ends.


**Use case: UC12 – Exit**

**Guarantees**
- Zenith exits gracefully.

**MSS**
1. Tutor requests to exit.
2. Zenith terminates.
Use case ends.


**Use case: UC13 – Autosave**

**Actor: File System**

**Preconditions**
- A data-modifying command has just succeeded.

**Guarantees**
- Latest state is written to local storage if write operation succeeds.
- User is notified of any errors during read or write operations.

**MSS**
1. Zenith attempts to write the updated data to the save file.
Use case ends.

**Extensions**
* 1a. Zenith encounters an error during write operation (e.g., due to file permissions or disk space).
    * 1a1. Zenith shows an error message to the user indicating the save operation failed.
    * 1a2. Zenith logs the error details.
    * 1a3. Application continues to function with in-memory data.
      Use case ends.
* 1b. Zenith encounters an error during read operation on startup (e.g., corrupted data file).
    * 1b1. Zenith shows an error message to the user indicating the data could not be loaded.
    * 1b2. Zenith logs the error details.
    * 1b3. Application starts with an empty address book.
      Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Autosave functionality must always preserve user data immediately after every data-changing command (add, delete, edit, session/subject changes) with no perceptible delay.
5.  Application startup, shutdown, and command responses for core actions (add, edit, delete, addsession, addsubject) should complete in under 1 second for databases with up to 1000 students on consumer-grade hardware.
6.  The application shall recover gracefully from partial file corruption, access errors, or disk space insufficiency, and never crashing silently or destroying unsaved user data.
7.  All error and warning messages must be concise, clear, and non-ambiguous, allowing users to recover independently without technical support.
8.  The _core functions_ of the application must work completely offline with no network dependencies, ensuring reliability regardless of internet connectivity.

### Glossary

* **CLI (Command Line Interface)**: Text-based interface where users interact with the application by typing commands rather than clicking buttons or menus.
* **GUI (Graphical User Interface)**: Form of user interface that allows users to interact with electronic devices through graphical icons and visual indicators such as secondary notation
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

2. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   2. Re-launch the app by running it through command line.<br>
       Expected: The most recent window size and location is retained.

### Adding a person

1. Adding a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.
      At the start of every new test(_new number_) case, run `clear` to ensure that the address book is empty.
   
   2. Test case 1(a): `add n/bill s/SEC2 p/83451234 e/bill@gmail.com a/yishun street 10`<br> 
      Expected: New contact named bill is added to the list
   
   3. Test case 1(b): `add n/bill s/SEC3 p/91203442 e/billbi@gmail.com a/yishun street 11`<br> 
      Expected: New contact named bill is added to the list
   
   4. Test case 2(a): `add n/bill s/SEC2 p/83451234 e/bill@gmail.com a/yishun street 10`<br> 
      Expected: New contact named bill is added to the list
   
   5. Test case 2(b): `add n/bill s/PRI5 p/83451234 e/billbo@gmail.com a/yishun street 12` <br>
      Expected: No new person is added to the list due to duplicate contacts. Error message indicating duplicate contact.
   
   6. Test case 3: `add n/bill s/PRI4 e/bill@gmail.com a/yishun street 10` <br>
      Expected: No new person is added to the list due to missing parameter. Error message indicating missing parameter.

### Listing all persons

1. Listing all persons in the address book

    1. Prerequisites: Have at least one person in the address book.

    2. Test case: `list`<br>
       Expected: All contacts are displayed in the person list. Result display shows the number of persons listed.

    3. Test case: After using `find John` to filter the list, use `list`<br>
       Expected: The filtered view is cleared and all contacts are shown again.

   4. Test case: `list extra parameters`<br>
      Expected: All contacts are displayed. Extra parameters are ignored.

### Finding persons by name

1. Finding persons whose names contain given keywords

    1. Prerequisites: Have multiple persons in the address book with different names.

    2. Test case: `find John`<br>
       Expected: All persons with "John" in their name (case-insensitive) are displayed. Result display shows the number of persons found.

    3. Test case: `find john doe`<br>
       Expected: All persons with either "john" or "doe" in their name are displayed (OR search).

    4. Test case: `find alex david`<br>
       Expected: Persons with "alex" or "david" in their names are shown.

    6. Test case: `find`<br>
       Expected: Error message indicating the keyword field cannot be empty.

    7. Test case: Partial matches like `find han` (should match "Hans")<br>
       Expected: All persons with "han" anywhere in their name are displayed.

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   2. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the result display.

   3. Test case: `delete 0`<br>
      Expected: No person is deleted. Error messaging indicating invalid index.

   4. Test case: `delete -1`<br>
      Expected: No person is deleted. Error message indicating invalid index.

   5. Test case: `delete x` (where x is larger than the list size)<br>
      Expected: No person is deleted. Error message indicating the index provided is out of bounds.

   6. Test case: `delete abc`<br>
      Expected: No person is deleted. Error message indicating invalid command format.

   7. Test case: `delete`<br>
      Expected: No person is deleted. Error message showing the correct command format.

2. Deleting a person from a filtered list

    1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

    2. Test case: Use `find John` to filter the list, then `delete 1`<br>
       Expected: First contact in the filtered list is deleted. Details of the deleted contact shown in the result display.

    3. Test case: Use `find John` to filter the list, then `delete 5` (where filtered list has fewer than 5 persons)<br>
       Expected: No person is deleted. Error message indicating the index provided is out of bounds.

    4. Test case: Use `find NonExistentName` to get an empty filtered list, then `delete 1`<br>
       Expected: No person is deleted. Error message indicating the index provided is out of bounds.


### Adding subject tags

1. Adding subject tags for a person while all persons are being shown

    1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.
       At the start of every new test case, run `clear` to ensure that the address book is empty.

    2. Test case: `addsubject 1 sub/MATH`<br>
       Expected: MATH subject tag is added to first contact. Success message shows subject tag added to contact. Subject tag displayed in the detailed view with color coding.

    3. Test case: `addsubject 1 sub/PHY sub/CHEM sub/BIO`<br>
       Expected: PHY, CHEM, and BIO subject tags are added to first contact. Success message lists all three subjects. Detailed view shows all three subject tags with color coding.

    4. Test case: `addsubject 1 sub/math`<br>
       Expected: MATH subject tag is added (case-insensitive). Success message shows subject tag added to contact. Subject tag appears in uppercase in the detailed view.

    5. Test case: `addsubject 1 sub/ENG`, then `addsubject 1 sub/ENG`<br>
       Expected: Second addsubject command fails. Error message indicates subject tags already exist for the contact".

    6. Test case: `addsubject 1 sub/MATH sub/MATH`<br>
       Expected: No subject is added. Error message indicates duplicate subject tag(s) detected in command. 

    7. Test case: `addsubject 0 sub/SCI`<br>
       Expected: No subject is added. Error message indicates invalid index.

    8. Test case: `addsubject 1 sub/INVALID`<br>
       Expected: No subject is added. Error message shows the list of valid subject codes.

    9. Test case: `addsubject 1 sub/`<br>
       Expected: No subject is added. Error message indicates subject cannot be blank and shows valid subject codes.

    10. Test case: `addsubject 1`<br>
       Expected: No subject is added. Error message shows command usage format.

    11. Test case: `addsubject 100 sub/HIST` (where 100 is larger than the list size)<br>
       Expected: No subject is added. Error message indicates invalid person index.

    12. Test case: `addsubject 1 sub/MATH`, then `addsubject 1 sub/PHY sub/CHEM`<br>
       Expected: PHY and CHEM are added successfully. Contact now has three subject tags (MATH, PHY, CHEM). Success message confirms addition of PHY and CHEM.

    13. Test case: `addsubject -1 sub/MATH`<br>
       Expected: No subject is added. Error message indicates invalid index.

    14. Test case: `addsubject abc sub/MATH`<br>
       Expected: No subject is added. Error message indicates invalid command format or invalid index.

    15. Other incorrect addsubject commands to try: `addsubject`, `addsubject x sub/MATH` (where x is non-numeric or larger than list size)<br>
       Expected: Error messages shown explaining the specific issue with the command format or parameters.

### Finding earliest free time slot

1. Finding a free time slot with invalid duration values

    1. Prerequisites: Have at least one person in the address book for adding sessions.

    2. Test case: `free 0`<br>
       Expected: Error message indicating invalid duration.

    3. Test case: `free -2`<br>
       Expected: Error message indicating invalid duration.

    4. Test case: `free`<br>
       Expected: Error message indicating missing duration parameter or showing correct command format.

2. Finding a free time slot with empty schedule

   1. Prerequisites: Run `clear` and confirm to ensure all contacts and sessions are removed from the address book.

   2. Test case: `free 2`<br>
      Expected: "The earliest free time is: MONDAY 08:00"

3. Finding a free time slot with boundary duration

   1. Prerequisites: Have minimal or no sessions in the address book.

   1. Test case: `free 14`<br>
      Expected: Returns earliest day with 14 consecutive free hours (e.g., "MONDAY 08:00") or "No free time available" if no such slot exists.

### Editing a person

1. Editing a person's contact details while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   2. Test case: `edit -c 1 n/John Tan p/91234567`<br>
      Expected: First contact's name and phone are updated. Updated contact details shown in the result display.

   3. Test case: `edit -c 1 p/98765432` (where contact 2 already has phone 98765432)<br>
      Expected: No person is updated. Error message: "A person with this phone number already exists in the address book."

   4. Test case: `edit -c 0 n/John`<br>
      Expected: No person is updated. Error message indicating invalid index shown.

   5. Test case: `edit -c 1 p/abc`<br>
      Expected: No person is updated. Error message indicating phone numbers should only contain numbers and be at least 3 digits long.

   6. Test case: `edit 1 n/John`<br>
      Expected: No person is updated. Error message: "A valid flag must be provided. Use -c for contact or -s for session."

   7. Test case: `edit -c 1 sub/`<br>
         Expected: All subjects are cleared from the first contact. Success message confirms subjects have been cleared.

   8. Test case: `edit -c 1 sub/ sub/`<br>
      Expected: No person is updated. Error message: "Multiple subject clear operations detected. Use 'sub/' only once to clear all subjects."

   9. Test case: `edit -c 1 sub/ sub/MATH`<br>
      Expected: No person is updated. Error message: "Cannot clear and edit subjects simultaneously. Use 'sub/' alone to clear, or provide subject values to replace."

   10. Other incorrect edit commands to try: `edit -c 1` (no fields), `edit -x 1 n/John` (invalid flag), `edit -c 999 n/John` (where 999 is larger than list size)<br>
      Expected: Error messages shown explaining the specific issue with the command format or parameters.

2. Editing a person's session timings while all persons are being shown

   1. Prerequisites: Have a contact with at least one session. List all persons using the `list` command.

   2. Test case: `edit -s 1 d/TUE s/1000 e/1200`<br>
      Expected: All sessions for the first contact are replaced with "TUE 1000-1200". Updated contact shown with new session timing.

   3. Test case: `edit -s 1 d/MON s/0900 e/1100 d/MON s/1030 e/1230`<br>
      Expected: No person is updated. Error message indicating sessions overlap.

   4. Test case: `edit -s 1 s/0900 d/MON e/1100`<br>
      Expected: No person is updated. Error message: "Invalid session format. Each session must follow order: d/, s/, e/."

   5. Test case: `edit -s 1 d/MON s/1200 e/1000`<br>
      Expected: No person is updated. Error message indicating start time must be before end time.

   6. Test case: `edit -s 0 d/MON s/0900 e/1100`<br>
      Expected: No person is updated. Error message indicating invalid index.

   7. Other incorrect session edit commands to try: `edit -s 1 d/MON s/0900` (incomplete triplet), `edit -s 1 d/MONDAY s/0900 e/1100` (invalid day format), `edit -s 999 d/MON s/0900 e/1100` (where 999 is larger than list size) `edit -s 1 d/MON s/0900 e/0800 clear/`(invalid command syntax) <br>
      Expected: Error messages shown explaining the specific issue with session format or parameters.

### Setting payment status

1. Setting payment status for a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   2. Test case: `setpayment 1 status/PAID`<br>
      Expected: First contact's payment status is set to PAID with default billing day 1. Updated payment details shown in the result display and in the detailed view panel.

   3. Test case: `setpayment 2 status/PENDING start/15`<br>
      Expected: Second contact's payment status is set to PENDING with billing start day 15. Updated payment details shown in the result display and in the detailed view panel.

   4. Test case: `setpayment 1 status/OVERDUE start/20`<br>
      Expected: First contact's payment status is set to OVERDUE with billing start day 20. Payment status shown as "OVERDUE (X days)" in the detailed view, where X is calculated based on billing cycle.

   5. Test case: `setpayment 1 status/paid`<br>
      Expected: First contact's payment status is set to PAID (case-insensitive). Status is accepted and updated successfully.

   6. Test case: `setpayment 0 status/PAID`<br>
      Expected: No person is updated. Error message indicating invalid index shown in the result display.

   7. Test case: `setpayment 1 status/INVALID`<br>
      Expected: No person is updated. Error message indicating payment status must be one of: PENDING, PAID, OVERDUE.

   8. Test case: `setpayment 1 status/PAID start/32`<br>
      Expected: No person is updated. Error message indicating billing start day must be between 1-31.

   9. Test case: `setpayment 1 status/PAID start/0`<br>
      Expected: No person is updated. Error message indicating billing start day must be between 1-31.

   10. Other incorrect setpayment commands to try: `setpayment`, `setpayment 1`, `setpayment x status/PAID` (where x is larger than the list size or non-numeric)<br>
      Expected: Error messages shown explaining the specific issue with the command format or parameters.

### Adding a session

1. Adding a session for a person while all persons are being shown

    1. Prerequisites: List all persons using the `list` command. Multiple persons(at least 2) in the list but none of them with session tag(e.g. MON 1100 - 1200). 
       At the start of every new test(_new number_), case run `clear` to ensure that the address book is empty.
   
    2. Test case 1(a): `addsession 1 d/Mon s/1100 e/1200`<br>
       Expected: New session will be added under the first person in the list.
   
    3. Test case 1(b): `addsession 2 d/Mon s/1100 e/1200`<br>
       Expected: New session will be added under the second person in the list.
   
    4. Test case 2(a): `addsession 1 d/Mon s/1100 e/1200`<br>
       Expected: New session will be added under the first person in the list.
   
    5. Test case 1(b): `addsession 1 d/Mon s/1130 e/1230`<br>
       Expected: No new session will be added due to overlapping sessions. Error message indicating sessions overlap.
   
    6. Test case 3: `addsession 1 d/Mon s/1130` <br>
       Expected: No new person is added to the list due to missing parameter. Error message indicating missing parameter.

### Clearing all entries

1. Clearing all contacts from the address book

    1. Prerequisites: Have at least one person in the address book.

   2. Test case: `clear` followed by `y`<br>
      Expected: After entering `clear`, a confirmation message "Are you sure you want to clear all data? (y/n)" is displayed. After entering `y`, all contacts are removed from the list. Result display shows success message. Person list is now empty.

   3. Test case: `clear` followed by `n`<br>
      Expected: After entering `clear`, a confirmation message is displayed. After entering `n`, the clear operation is cancelled. Result display shows command cancellation. All contacts remain in the list.

   4. Test case: `clear` followed by invalid input (e.g., `abc`, `yes`, `no`)<br>
      Expected: After entering `clear`, a confirmation message is displayed. After entering invalid input, result display shows "Invalid input. Please try 'clear' again, then enter 'y' or 'n'.". All contacts remain in the list.

   5. Test case: `clear` on an already empty list, followed by `y`<br>
      Expected: Confirmation message is displayed. After entering `y`, list remains empty. Success message still shown.

   6. Test case: `clear extra parameters`<br>
      Expected: Confirmation message is displayed (extra parameters are ignored). Entering `y` will clear all contacts, entering `n` will cancel the operation.

   7. Test case: After `clear` and `y`, try `list`<br>
      Expected: Empty list displayed with "0 persons listed!" message.

<box type="warning" seamless>

**Note:** The `clear` command is irreversible and will remove all data from the data file.

</box>

### Saving data

1. Simulating missing/corrupted data files
   1. Open the app
   2. Do at least one operation (e.g. list)
   3. Close the app
   4. There should be a data folder in the same directory as the jar file
   5. Open the zenith.json file found within the data folder
   6. Within the outermost curly brace, input "break" anywhere
   7. The next time the app is opened, there will be a prompt indicating that there were some problems loading the data file
   8. The application will be opened with an empty list

## **Appendix: Effort**

* Difficulty and Challenges faced
  * Trying to integrate sessions was challenging as it meant that we had to keep track of how each operation that added or removed a session for a particular individual would affect other sessions in the address book. 


* Effort and Achievements of the project
  * Extended Tags with a `SessionTag` class which allow for easy identification and keep track of the `Session` associated to the tag.
  * Extended AddressBook with `WeeklySessions` that will keep track of all the unique sessions within a week to allow for scheduling operations to be performed.
  * Implemented a payment tracking system with three statuses (PENDING, PAID, OVERDUE), customizable billing cycles (1-31 days), automatic days overdue calculation


## **Appendix: Planned enhancements**

Team size: 5

1. **Users cannot specify a time period when using the Free Command**
   
   **Current Limitation:** The `free` command currently returns the earliest available time slot that can accommodate the specified `DURATION`, starting from the beginning of the week. However, tutors may want to find the earliest free time slot starting from a specific day or time, rather than just the absolute earliest slot available.

   **Planned Enhancement:** We plan to extend the `free` command with optional parameters to specify a starting point for the search:
    * Add an optional `day/DAY` parameter to search from a specific day onwards
    * Add an optional `time/TIME` parameter to search from a specific time onwards
    * Example: `free 2 day/WED time/1400` would find the earliest 2-hour slot starting from Wednesday at 2:00 PM or later

   This would allow tutors to:
    * Find availability within a specific timeframe (e.g., "What's my next free slot after Tuesday afternoon?")
    * Plan sessions around existing commitments more effectively
    * Accommodate student preferences for certain days or times

2. **Extend session timing range beyond 0800-2200 with customizable resting hours**

   **Current Limitation:** All sessions are currently limited to timings between 0800 (8:00AM) and 2200 (10:00PM). This restriction may not accommodate tutors who conduct early morning sessions (before 8AM) or late evening sessions (after 10PM).

   **Planned Enhancement:** We plan to extend the acceptable time range to support the full 24-hour period (0000 to 2359), with the ability for tutors to set customizable "Resting hours".

   Tutors will be able to define their personal resting hours (e.g., 2300-0700 for overnight rest), and the system will:
    * Prevent `addsession` from scheduling sessions that fall within the defined resting hours
    * Exclude resting hours when the `free` command searches for available time slots
    * Allow tutors to modify their resting hours as their schedule changes

   This gives tutors full flexibility to personalize Zenith around their tutoring working hours, as we understand that different tutors have different work arrangements and availability.

3. **Automatic payment status update from PENDING/PAID to OVERDUE**

   **Current Limitation:**
   The `setpayment` command currently requires manual updates when payments become overdue. The system does not
   automatically transition a PENDING or PAID status to OVERDUE when the billing cycle passes.

   **Planned Enhancement:**
   We plan to implement automatic status checking that updates payment statuses to OVERDUE when the billing cycle date
   is reached. 

   Tutors will be able to:
    * Have payments automatically marked as OVERDUE when the billing date passes
    * Track payment status accuracy without manual intervention
    * Receive notifications about newly overdue payments

4. **Expand Study Year Parameter Scope**

   **Current Limitation:** The study year parameter is restricted to predefined academic levels (PRI, SEC, JC, POLY, UNI) with specific year numbers. This does not accommodate individuals who are not pursuing formal education or those in alternative education pathways.

   **Planned Enhancement:** Introduce more flexible categorization options to support diverse student backgrounds and tutoring scenarios.

   Tutors will be able to:
    - Add contacts who are not currently enrolled in traditional educational institutions
    - Categorize students in alternative education pathways not covered by current categories
    - Use flexible labels like "N/A" or "Other" for cases that don't fit standard academic classifications
