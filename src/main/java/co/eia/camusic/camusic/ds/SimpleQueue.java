package co.eia.camusic.camusic.ds;

import co.eia.camusic.camusic.ds.interfaces.PlaylistStructure;
import co.eia.camusic.camusic.ds.nodes.QueueNode;
import co.eia.camusic.camusic.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SimpleQueue implements PlaylistStructure<Song> {
    private QueueNode<Song> front;
    private QueueNode<Song> rear;
    private int size;

    public SimpleQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }


    @Override
    public void add(Song element) {
        enqueue(element);
    }

    @Override
    public Song remove(Song element) {
        if(element == null || isEmpty()) return null;

        QueueNode<Song> previous = null;
        QueueNode<Song> current = front;

        while(current != null){
            if(current.getData().equals(element)){
                Song removed = current.getData();

                if(previous == null){
                    front = current.getNext();
                } else {
                    previous.setNext(current.getNext());
                }

                if(current == rear) rear = previous;

                current.setNext(null);
                size--;

                if(size == 0){
                    clear();
                }

                return removed;
            }

            previous = current;
            current = current.getNext();
        }

        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public boolean contains(Song element) {
        if(element == null || isEmpty()) return false;

        QueueNode<Song> node = front;

        while(node != null){
            if(node.getData().equals(element)) return true;
            node = node.getNext();
        }

        return false;
    }

    @Override
    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }

    @Override
    public List<Song> toList() {
        List<Song> songs = new ArrayList<>();

        QueueNode<Song> current = front;

        while(current != null){
            songs.add(current.getData());
            current = current.getNext();
        }

        return songs;
    }

     //FIFO METHODS
     public void enqueue(Song song) {
         validateSong(song);

         QueueNode<Song> newNode = new QueueNode<>(song);

         if (isEmpty()) {
             front = newNode;
             rear = newNode;
             size = 1;
             return;
         }

         rear.setNext(newNode);
         rear = newNode;
         size++;
     }

    public Song dequeue(){
        if(isEmpty()) return null;

        QueueNode<Song> oldFront = front;
        Song removed = oldFront.getData();

        front = oldFront.getNext();
        oldFront.setNext(null);
        size--;

        if(size == 0) rear = null;

        return removed;
    }

    public Song peek(){
        if(isEmpty()) return null;

        return front.getData();
    }


    //AUXILIARY METHODS
    private void validateSong(Song song){
        if(song == null) throw new IllegalArgumentException("Song cannot be null");
    }
}
