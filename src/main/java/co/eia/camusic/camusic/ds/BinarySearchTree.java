package co.eia.camusic.camusic.ds;

import co.eia.camusic.camusic.ds.interfaces.PlaylistStructure;
import co.eia.camusic.camusic.ds.nodes.TreeNode;
import co.eia.camusic.camusic.model.Song;

import java.util.ArrayList;
import java.util.List;

public class BinarySearchTree implements PlaylistStructure<Song> {
    private TreeNode<Song> root;
    private int size;
    private Song currentSong;

    public BinarySearchTree() {
        this.root = null;
        this.size = 0;
        this.currentSong = null;
    }

    // PLAYLIST STRUCTURE METHODS
    @Override
    public void add(Song element) {
        insert(element);
    }

    @Override
    public Song remove(Song element) {
        TreeNode<Song> node = findNode(element);
        if (node == null) return null;

        Song removed = node.getData();
        root = removeNode(root, element);
        size--;

        if(currentSong != null && currentSong.equals(removed)){
            currentSong = isEmpty() ? null : getFirst();
        }
        return removed;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(Song element) {
        return findNode(element) != null;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
        currentSong = null;
    }

    @Override
    public List<Song> toList() {
        List<Song> songs = new ArrayList<>();
        inorder(root, songs);
        return songs;
    }

    @Override
    public Song current() {
        if(isEmpty()) return null;

        if(currentSong == null || findNode(currentSong) == null) currentSong = getFirst();

        return currentSong;
    }

    @Override
    public Song next() {
        if(isEmpty()) return null;

        if(currentSong == null || findNode(currentSong) == null){
            currentSong = getFirst();
            return currentSong;
        }

        Song successor = successorOf(currentSong);

        currentSong = successor != null ? successor : getFirst();

        return currentSong;
    }

    @Override
    public Song previous() {
        if (isEmpty()) {
            return null;
        }

        if (currentSong == null || findNode(currentSong) == null) {
            currentSong = getLast();
            return currentSong;
        }

        Song predecessor = predecessorOf(currentSong);

        currentSong = predecessor != null ? predecessor : getLast();

        return currentSong;
    }

    // TREE METHODS
    public void insert(Song song) {
        validateSong(song);
        root = insertNode(root, song);

        if(currentSong == null) currentSong = song;
    }

    public Song find(Song song) {
        TreeNode<Song> node = findNode(song);
        return node == null ? null : node.getData();
    }

    public List<Song> findByName(String name) {
        List<Song> result = new ArrayList<>();
        findByName(root, name, result);
        return result;
    }

    public Song getFirst() {
        TreeNode<Song> minimum = findMinimum(root);
        return minimum == null ? null : minimum.getData();
    }

    public Song getLast() {
        TreeNode<Song> maximum = findMaximum(root);
        return maximum == null ? null : maximum.getData();
    }

    public List<Song> toReverseList() {
        List<Song> songs = new ArrayList<>();
        reverseInorder(root, songs);
        return songs;
    }

    public int height() {
        return height(root);
    }

    // AUXILIARY METHODS
    private void validateSong(Song song) {
        if (song == null) throw new IllegalArgumentException("Song cannot be null");
    }

    private int compareSongs(Song first, Song second) {
        int comparison = first.getName().compareToIgnoreCase(second.getName());
        if (comparison != 0) return comparison;

        comparison = first.getArtist().compareToIgnoreCase(second.getArtist());
        if (comparison != 0) return comparison;

        return first.getId().compareTo(second.getId());
    }

    private TreeNode<Song> insertNode(TreeNode<Song> current, Song song) {
        if (current == null) {
            size++;
            return new TreeNode<>(song);
        }

        int comparison = compareSongs(song, current.getData());
        if (comparison < 0) {
            current.setLeft(insertNode(current.getLeft(), song));
        } else if (comparison > 0) {
            current.setRight(insertNode(current.getRight(), song));
        } else {
            throw new IllegalArgumentException("This exact song already exists in the tree");
        }

        return current;
    }

    private TreeNode<Song> findNode(Song song) {
        if (song == null) return null;
        return findNode(root, song);
    }

    private TreeNode<Song> findNode(TreeNode<Song> current, Song song) {
        if (current == null) return null;

        int comparison = compareSongs(song, current.getData());
        if (comparison == 0) return current;
        if (comparison < 0) return findNode(current.getLeft(), song);
        return findNode(current.getRight(), song);
    }

    private void findByName(TreeNode<Song> current, String name, List<Song> result) {
        if (current == null || name == null) return;

        int comparison = name.compareToIgnoreCase(current.getData().getName());
        if (comparison == 0) {
            result.add(current.getData());

            // Puede haber más coincidencias en ambos subárboles
            findByName(current.getLeft(), name, result);
            findByName(current.getRight(), name, result);
        } else if (comparison < 0) {
            findByName(current.getLeft(), name, result);
        } else {
            findByName(current.getRight(), name, result);
        }
    }

    private void inorder(TreeNode<Song> current, List<Song> songs) {
        if (current == null) return;

        inorder(current.getLeft(), songs);
        songs.add(current.getData());
        inorder(current.getRight(), songs);
    }

    private void reverseInorder(TreeNode<Song> current, List<Song> songs) {
        if (current == null) return;

        reverseInorder(current.getRight(), songs);
        songs.add(current.getData());
        reverseInorder(current.getLeft(), songs);
    }

    private TreeNode<Song> findMinimum(TreeNode<Song> current) {
        if (current == null) return null;
        if (current.getLeft() == null) return current;
        return findMinimum(current.getLeft());
    }

    private TreeNode<Song> findMaximum(TreeNode<Song> current) {
        if (current == null) return null;
        if (current.getRight() == null) return current;
        return findMaximum(current.getRight());
    }

    private TreeNode<Song> removeNode(TreeNode<Song> current, Song song) {
        if (current == null) return null;

        int comparison = compareSongs(song, current.getData());
        if (comparison < 0) {
            current.setLeft(removeNode(current.getLeft(), song));
            return current;
        }

        if (comparison > 0) {
            current.setRight(removeNode(current.getRight(), song));
            return current;
        }

        if (current.getLeft() == null) return current.getRight();

        if (current.getRight() == null) return current.getLeft();

        TreeNode<Song> successor = findMinimum(current.getRight());
        current.setData(successor.getData());
        current.setRight(removeNode(current.getRight(), successor.getData()));
        return current;
    }

    private int height(TreeNode<Song> current) {
        if (current == null) return 0;

        int leftHeight = height(current.getLeft());
        int rightHeight = height(current.getRight());
        return Math.max(leftHeight, rightHeight) + 1;
    }

    private Song successorOf(Song song) {
        TreeNode<Song> node = findNode(root, song);
        if (node == null) return null;

        if(node.getRight() != null) return findMinimum(node.getRight()).getData();

        TreeNode<Song> ancestor = root;
        TreeNode<Song> successor = null;

        while(ancestor != node){
            int comparison = compareSongs(ancestor.getData(), song);

            if(comparison < 0){
                successor = ancestor;
                ancestor = ancestor.getLeft();
            } else {
                ancestor = ancestor.getRight();
            }
        }
        return successor == null ? null : successor.getData();
    }

    private Song predecessorOf(Song song){
        TreeNode<Song> node = findNode(root, song);

        if(node == null) return null;

        if(node.getLeft() != null) return findMaximum(node.getLeft()).getData();

        TreeNode<Song> ancestor = root;
        TreeNode<Song> predecessor = null;

        while(ancestor != node){
            int comparison = compareSongs(ancestor.getData(), song);

            if(comparison > 0){
                predecessor = ancestor;
                ancestor = ancestor.getRight();
            } else {
                ancestor = ancestor.getLeft();
            }
        }
        return predecessor == null ? null : predecessor.getData();
    }
}
