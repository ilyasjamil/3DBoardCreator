package ibis.boardcreator.ui;

import java.util.Stack;

import ibis.boardcreator.datamodel.Grid;

public class UndoRedoHandler {

	private Stack<Grid> undoStack, redoStack;
	// invariant: The top state of the undoStack always is a copy of the
	// current state of the grid.

	public UndoRedoHandler(Grid startState) { 
		undoStack = new Stack<Grid>();
		redoStack = new Stack<Grid>();

		// store the initial state of the grid on the undo stack
		undoStack.push(startState.clone());
	}

	/**
	 * saves the current state of the grid for later restoration
	 */
	public void saveState(Grid state) {
		undoStack.push(state.clone());
		redoStack.clear();
	}

	/**
	 * Returns the previous state from the undo stack
	 * if there's only the current state on the stack, 
	 * it gets returned but not removed from the undo stack 
	 */
	public Grid undo() {
		if (undoStack.size() == 1) // only the current state is on the stack
			return undoStack.peek();

		Grid currentState = undoStack.pop();
		redoStack.push(currentState);
		
		return undoStack.peek();
	}
	
	/**
	 * returns the state of the grid from before the last undo
	 * action was performed. If some change was made to the Grid
	 * since the last undo, then this method just returns the current state.
	 */
	public Grid redo() {
		if (redoStack.isEmpty())
			return undoStack.peek();

		Grid currentState = redoStack.pop();
		undoStack.push(currentState);
		return currentState;
	}

}
