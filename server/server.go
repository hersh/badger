package main

import (
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net/http"
	"os"
	"path"
	"unicode/utf8"
)

type Node struct {
	Name string
	Children []*Node
}

type MusicService struct {
	music_path_root string
	song_tree *Node
}

// filename is relative to ms.music_path_root
func (ms *MusicService) scanFile(filename string) (*Node, error) {
	if path.Ext(filename) != ".mp3" {
		return nil, errors.New("Skipping non-mp3 file")
	}
	if !utf8.ValidString(filename) {
		return nil, errors.New(fmt.Sprintf("Invalid utf8 in filename %s", filename))
	}
	node := new(Node)
	node.Name = filename
	return node, nil
}

// filename is relative to ms.music_path_root
func (ms *MusicService) scanDir(filename string) (*Node, error) {
	if !utf8.ValidString(filename) {
		return nil, errors.New(fmt.Sprintf("Invalid utf8 in filename %s", filename))
	}
	fullname := path.Join(ms.music_path_root, filename)
	
	file, err := os.Open(fullname)
	if err != nil {
		return nil, err
	}
	children, err := file.Readdir(0)
	if err != nil {
		return nil, err
	}
	node := new(Node)
	node.Name = filename
	node.Children = make([]*Node, 0, len(children))

	for _, child := range(children) {
		child_path := path.Join(filename, child.Name())
		fmt.Printf("child is %s\n", child_path)
		var child_node *Node;
		if child.IsDir() {
			child_node, err = ms.scanDir(child_path)
		} else {
			child_node, err = ms.scanFile(child_path)
		}
		if child_node != nil {
			node.Children = append(node.Children, child_node)
		}
	}
	if len(node.Children) == 0 {
		return nil, nil
	}
	return node, nil
}

func (ms *MusicService) ScanMusic() {
	var err error
	ms.song_tree, err = ms.scanDir("")
	if err != nil {
		fmt.Printf("Error scanning music: %s\n", err.Error())
	}
}

func (ms *MusicService) listHandler(w http.ResponseWriter, r *http.Request) {
	json_data, err := json.MarshalIndent(ms.song_tree, "", " ")
	if err != nil {
		fmt.Fprintf(w, "Error encoding data: %s\n", err.Error())
		return
	}
	w.Write(json_data)
}

func (ms *MusicService) getHandler(w http.ResponseWriter, r *http.Request) {
	filename := ms.music_path_root + "/" + r.URL.Path[5:] // 5 is length of '/get/'
	file, err := os.Open(filename)
	if err != nil {
		fmt.Fprintf(w, "404 Error: %s\n", err.Error())
                return
	}
	fileinfo, err := file.Stat()
	if err != nil {
		fmt.Fprintf(w, "404 Error: %s\n", err.Error())
                return
	}
	if fileinfo.IsDir() {
		fmt.Fprintf(w, "404 Error: %s is a directory, not a file\n", filename)
                return
	}
	io.Copy(w, file)
}

func main() {
	if len(os.Args) < 2 {
		panic(fmt.Sprintf("Usage: %s path/to/music", os.Args[0]))
	}
	service := new(MusicService)
	service.music_path_root = os.Args[1]
	service.ScanMusic()

	http.HandleFunc("/list", service.listHandler)
	http.HandleFunc("/get/", service.getHandler)
	http.ListenAndServe(":8080", nil)
}
