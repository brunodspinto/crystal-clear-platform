package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.TaskCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Task category repository.
 */
public class TaskCategoryRepository {
    private final List<TaskCategory> taskCategories;

    /**
     * Instantiates a new Task category repository.
     */
    public TaskCategoryRepository() {
        taskCategories = new ArrayList<>();
    }

    /**
     * This method returns an exsiting Task Category by its description.
     *
     * @param taskCategoryDescription The description of the task category to be created.
     * @return The task category.
     * @throws IllegalArgumentException if the task category does not exist, which should never happen.
     */
    public TaskCategory getTaskCategoryByDescription(String taskCategoryDescription) {
        TaskCategory newTaskCategory = new TaskCategory(taskCategoryDescription);
        TaskCategory taskCategory = null;
        if (taskCategories.contains(newTaskCategory)) {
            taskCategory = taskCategories.get(taskCategories.indexOf(newTaskCategory));
        }
        if (taskCategory == null) {
            throw new IllegalArgumentException(
                    "Task Category requested for [" + taskCategoryDescription + "] does not exist.");
        }
        return taskCategory;
    }

    /**
     * Adds a task category if no duplicate exists.
     *
     * @param taskCategory the task category to add.
     * @return the stored (cloned) task category if added; {@code null} if a duplicate exists.
     */
    public TaskCategory add(TaskCategory taskCategory) {
        if (!validateTaskCategory(taskCategory)) {
            return null;
        }
        TaskCategory clone = taskCategory.clone();
        if (taskCategories.add(clone)) {
            return clone;
        }
        return null;
    }

    private boolean validateTaskCategory(TaskCategory taskCategory) {
        return !taskCategories.contains(taskCategory);
    }

    /**
     * This method returns a defensive (immutable) copy of the list of task categories.
     *
     * @return The list of task categories.
     */
    public List<TaskCategory> getTaskCategories() {
        //This is a defensive copy, so that the repository cannot be modified from the outside.
        return List.copyOf(taskCategories);
    }
}