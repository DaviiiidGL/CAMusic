package co.eia.camusic.camusic.ds.interfaces;

import co.eia.camusic.camusic.model.Song;

import java.util.List;

public interface PlaylistStructure<T> {
    //Add a new element to the structure
    void add(T element);

    //Remove a specific element from the structure
    Song remove(T element);

    // Number of elements currently stored
    int size();

    // true if the structure is empty
    boolean isEmpty();

    // true if the structure contains the element
    boolean contains(T element);

    //Removes every element from the structure
    void clear();

    //Returns current elements as a plain list
    List<T> toList();

    Song current();
    Song next();
    Song previous();
}
