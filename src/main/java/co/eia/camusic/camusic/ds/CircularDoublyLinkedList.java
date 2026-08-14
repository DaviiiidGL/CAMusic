package co.eia.camusic.camusic.ds;

import co.eia.camusic.camusic.ds.interfaces.PlaylistStructure;
import co.eia.camusic.camusic.ds.nodes.DoubleNode;
import co.eia.camusic.camusic.model.Song;

import java.util.ArrayList;
import java.util.List;

public class CircularDoublyLinkedList implements PlaylistStructure<Song> {
    private DoubleNode<Song> head;
    private DoubleNode<Song> current;
    private int size;

    public CircularDoublyLinkedList() {
        this.head = null;
        this.current = null;
        this.size = 0;
    }

    // PLAYLIST STRUCTURE METHODS
    @Override
    public void add(Song element) {
        insertLast(element);
    }

    @Override
    public Song remove(Song element) {
        if(element == null || isEmpty()) return null;

        DoubleNode<Song> node = head;

        for(int i=0; i<size; i++){
            if(node.getData().equals(element)){
                Song removed = node.getData();

                if(size == 1){
                    clear();
                    return removed;
                }

                DoubleNode<Song> prev = node.getPrev();
                DoubleNode<Song> next = node.getNext();

                prev.setNext(next);
                next.setPrev(prev);

                if(node == head) head = next;
                if(node == current) current = next;

                node.setPrev(null);
                node.setNext(null);

                size--;

                return removed;
            }

            node = node.getNext();
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Song element) {
        if(element == null || isEmpty()) return false;

        DoubleNode<Song> node = head;

        for(int i=0; i<size; i++){
            if(node.getData().equals(element)) return true;
            node = node.getNext();
        }
        return false;
    }

    @Override
    public void clear() {
        head = null;
        current = null;
        size = 0;
    }

    @Override
    public List<Song> toList() {
        List<Song> songs = new ArrayList<>();

        if(isEmpty()) return songs;

        DoubleNode<Song> node = head;

        for(int i=0; i<size; i++){
            songs.add(node.getData());
            node = node.getNext();
        }

        return songs;
    }

    @Override
    public Song current() {
        if(isEmpty() || current == null) return null;
        return current.getData();
    }

    @Override
    public Song next() {
        if(isEmpty() || current == null) return null;
        current = current.getNext();
        return current.getData();
    }

    @Override
    public Song previous() {
        if(isEmpty() || current == null) return null;
        current = current.getPrev();
        return current.getData();
    }

    // CDLL METHODS
    public void insertFirst(Song song){
        validateSong(song);

        DoubleNode<Song> newNode = new DoubleNode<>(song);

        if(isEmpty()) {
            initializeWith(newNode);
            return;
        }

        DoubleNode<Song> tail = head.getPrev();
        linkBetween(tail, head, newNode);
        head = newNode;
        size++;
    }

    public void insertAfterCurrent(Song song){
        validateSong(song);

        if(isEmpty()){
            insertLast(song);
            return;
        }

        DoubleNode<Song> newNode = new DoubleNode<>(song);
        DoubleNode<Song> next = current.getNext();

        linkBetween(current, next, newNode);
        size++;
    }

    public void insertAt(Song song, int index){
        validateSong(song);

        if(index < 0) throw new IndexOutOfBoundsException("Index out of bounds");

        if(index == 0){
            insertFirst(song);
            return;
        }

        if(index >= size){
            insertLast(song);
            return;
        }

        DoubleNode<Song> newNode = new DoubleNode<>(song);
        DoubleNode<Song> next = head;

        for(int i=0; i<index-1; i++) next = next.getNext();

        linkBetween(next, next.getNext(), newNode);
        size++;
    }

    public void insertLast(Song song){
        validateSong(song);

        DoubleNode<Song> newNode = new DoubleNode<>(song);

        if(isEmpty()){
            initializeWith(newNode);
            return;
        }

        DoubleNode<Song> tail = head.getPrev();

        linkBetween(tail, head, newNode);
        size++;
    }

    public Song removeFirst(){
        if(isEmpty()) return null;

        Song removed = head.getData();

        if(size == 1){
            clear();
            return removed;
        }

        DoubleNode<Song> oldHead = head;
        DoubleNode<Song> newHead = oldHead.getNext();
        DoubleNode<Song> tail = oldHead.getPrev();

        tail.setNext(newHead);
        newHead.setPrev(tail);

        head = newHead;

        if(current == oldHead) current = newHead;

        oldHead.setNext(null);
        oldHead.setPrev(null);

        size--;

        return removed;
    }

    public Song removeLast(){
        if(isEmpty()) return null;

        DoubleNode<Song> oldTail = head.getPrev();
        Song removed = oldTail.getData();

        if(size == 1){
            clear();
            return removed;
        }

        DoubleNode<Song> newTail = oldTail.getPrev();

        newTail.setNext(head);
        head.setPrev(newTail);

        if(current == oldTail) current = head;

        oldTail.setPrev(null);
        oldTail.setNext(null);

        size--;

        return removed;


    }

    public Song removeCurrent(){
        if(isEmpty() || current==null) return null;

        Song removed = current.getData();

        if(size == 1){
            clear();
            return removed;
        }

        DoubleNode<Song> nodeToRemove = current;
        DoubleNode<Song> previous = nodeToRemove.getPrev();
        DoubleNode<Song> next = nodeToRemove.getNext();

        previous.setNext(next);
        next.setPrev(previous);

        if(nodeToRemove == head) head = next;

        current = next;

        nodeToRemove.setNext(null);
        nodeToRemove.setPrev(null);

        size--;

        return removed;
    }

    // AUXILIAR METHODS
    private void linkBetween(DoubleNode<Song> previous, DoubleNode<Song> next, DoubleNode<Song> newNode){
        newNode.setPrev(previous);
        newNode.setNext(next);

        previous.setNext(newNode);
        next.setPrev(newNode);
    }

    private void initializeWith(DoubleNode<Song> node){
        node.setNext(node);
        node.setPrev(node);

        head = node;
        current = node;
        size = 1;
    }

    private void validateSong(Song song){
        if(song == null) throw new IllegalArgumentException("Song cannot be null");
    }
}
