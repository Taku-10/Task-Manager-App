# Task-Manager-App
This project management system is a Java console application that allows users to manage a list of tasks in a project, including handling task dependencies. The system reads task information and dependencies from a text file, performs various operations, and saves the updated information back to the file. It also provides functionality to find task sequences and earliest commencement times.

## Features
Read Tasks from File: The application allows users to enter the name of a text file containing task information and dependencies. It reads the information from the file and populates the project management system.

Add Task: Users can add a new task with its time needed to complete and other tasks that the task depends on to the project.

Remove Task: Users can remove a task from the project.

Change Completion Time: Users can change the time needed to complete a task in the project.

Save to File: The system can save the (updated) task information and dependencies back to another textfile

Find Task Sequence: The application can find a sequence of tasks that does not violate any task dependency 

Find Earliest Commencement Time: The system can find the earliest possible commencement time for each task in the project and save the solution into a text file called EarliestTimes.txt.

## The text file containing task information and dependencies should be formatted as follows:

T1, 50

T2, 30, T1

T3, 40, T2, T4

T4, 90, T1, T7

T5, 70, T2, T4

T6, 55, T5

T7, 50

T8, 30, T4, T2


