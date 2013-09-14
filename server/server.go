package main

import (
	"encoding/json"
	"fmt"
	"id3"
	"io"
	"net/http"
	"os"
	"path/filepath"
)

type SongInfo struct {
	Title, Album, Artist, Filename string
	full_filename string
}

type MusicService struct {
	music_path_root string
	songs []*SongInfo
}

func (ms *MusicService) ScanSongFile(path string) (*SongInfo, error) {
	song := new(SongInfo)
	song.full_filename = path
	song.Filename = song.full_filename[len(ms.music_path_root)+1:]

	var fd, err = os.Open(path)
	if err != nil {
		return song, err
	}
	defer fd.Close()
	id3_data := id3.Read(fd)

	if id3_data != nil {
		song.Title = id3_data.Name
		song.Album = id3_data.Album
		song.Artist = id3_data.Artist
	}
	return song, nil
}

func (ms *MusicService) scanVisit(path string, info os.FileInfo, err error) error {
	if filepath.Ext(path) == ".mp3" {
		song, err := ms.ScanSongFile(path)
		if err != nil {
			fmt.Printf("Error: %s\n", err.Error())
		} else {
			ms.songs = append(ms.songs, song)
			fmt.Printf("Scanned file %s\n", path)
		}
	}
	return nil
}

func (ms *MusicService) ScanMusic() {
	ms.songs = make([]*SongInfo, 0, 1000)
	filepath.Walk(ms.music_path_root, ms.scanVisit)
	fmt.Printf("len(ms.songs) = %d\n", len(ms.songs))
}

func (ms *MusicService) listHandler(w http.ResponseWriter, r *http.Request) {
	json_data, err := json.MarshalIndent(ms.songs, "", " ")
	if err != nil {
		fmt.Fprintf(w, "Error encoding data: %s\n", err.Error())
		return
	}
	w.Write(json_data)
}

func (ms *MusicService) getHandler(w http.ResponseWriter, r *http.Request) {
	filename := ms.music_path_root + "/" + r.URL.Path[5:]
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
