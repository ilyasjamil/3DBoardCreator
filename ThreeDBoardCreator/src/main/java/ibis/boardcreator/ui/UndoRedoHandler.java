package ibis.boardcreator.ui;

import java.util.Stack;

import ibis.boardcreator.datamodel.Grid;
import ibis.boardcreator.ui.MainEditorController.EditorState;

public class UndoRedoHandler {

	private Stack<MainEditorController.EditorState> undoStack, redoStack;
	// invariant: The top state of the undoStack always is a copy of the
	// current state of the grid.


	public UndoRedoHandler(MainEditorController.EditorState startState) {
		undoStack = new Stack<MainEditorController.EditorState>();
		redoStack = new Stack<MainEditorController.EditorState>();

		// store the initial state of the canvas on the undo stack
		
		undoStack.push(startState);
		System.out.println("after constructor: " +undoStack.size());
		System.out.println("a c undo stack: " +undoStack);

	}

	/**
	 * saves the current state of the grid for later restoration
	 */
	public void saveState(MainEditorController.EditorState curState) {
		System.out.println("before save: " +undoStack.size());
		System.out.println("bef save undo stack: " +undoStack);
		undoStack.push(curState);
		redoStack.clear();
	}

	/**
	 * Returns the previous state from the undo stack
	 * if there's only the current state on the stack, 
	 * it gets returned but not removed from the undo stack 
	 */
	public EditorState undo() {
		System.out.println("undo: " +undoStack.size());
		System.out.println("undo stack: " +undoStack);
		if (undoStack.size() == 1) // only the current state is on the stack
			return undoStack.peek();

		EditorState currentState = undoStack.pop();
		redoStack.push(currentState);
		
		return undoStack.peek();
	}
	
	/**
	 * returns the state of the grid from before the last undo
	 * action was performed. If some change was made to the Grid
	 * since the last undo, then this method just returns the current state.
	 */
	public EditorState redo() {
		System.out.println("redo: " +undoStack.size());
		if (redoStack.isEmpty())
			return undoStack.peek();

		EditorState currentState = redoStack.pop();
		undoStack.push(currentState);
		return currentState;
		
	}

}
