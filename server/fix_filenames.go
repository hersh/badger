package main

import (
	"bufio"
	"fmt"
	"os"
	"path/filepath"
	"strings"
	"unicode/utf8"
)

func toUtf8(iso8859_1_buf []byte) string {
	buf := make([]rune, len(iso8859_1_buf))
	for i, b := range iso8859_1_buf {
		buf[i] = rune(b)
	}
	return string(buf)
}

func fixFile(path string) {
	iso_path := []byte(path)
	utf8_path := toUtf8(iso_path)
	fmt.Printf("Convert old path:\n")
	fmt.Printf("  %s to new path:\n", path)
	fmt.Printf("  %s?\n", utf8_path)

	valid_response := false
	fix := false
	for !valid_response {
		fmt.Printf("[Y]es or no? ")

		buf_stdin := bufio.NewReader(os.Stdin)
		line, _, err := buf_stdin.ReadLine()
		if err != nil {
			fmt.Printf("Error reading from stdin: %s\n", err.Error())
			return
		}
		line_str := strings.ToLower(string(line))
		switch line_str {
		case "", "yes", "y": valid_response, fix = true, true
		case "no", "n": valid_response, fix = true, false
		}
	}
	if fix {
		if err := os.Rename(path, utf8_path); err != nil {
			fmt.Printf("Error renaming file: %s\n", err)
		}
	}
	fmt.Printf("\n\n")
}

func visit(path string, info os.FileInfo, err error) error {
	if filepath.Ext(path) == ".mp3" {
		if !utf8.ValidString(path) {
			fixFile(path)
		}
	}
	return nil
}

func main() {
	if len(os.Args) < 2 {
		fmt.Printf("Usage: %s path/to/music", os.Args[0])
		return
	}
	filepath.Walk(os.Args[1], visit)
}
