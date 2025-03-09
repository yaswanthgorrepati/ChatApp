import React, { useState } from "react";
import "../styles/PrivateChat.css";

const UserSearch = ({ token, onSelectUser }) => {
  const [searchText, setSearchText] = useState("");
  const [results, setResults] = useState([]);

  const handleSearch = async () => {
    if (!searchText.trim()) return;
    try {
      const response = await fetch(
        `http://localhost:8080/users/search?q=${encodeURIComponent(
          searchText
        )}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
          },
        }
      );
      if (response.ok) {
        const users = await response.json();
        setResults(users);
      } else {
        console.error("Failed to search users");
      }
    } catch (error) {
      console.error("Search error:", error);
    }
  };

  return (
    <div className="user-search-container">
      <input
        type="text"
        placeholder="Search users..."
        value={searchText}
        onChange={(e) => setSearchText(e.target.value)}
        className="user-search-input"
      />
      <button onClick={handleSearch} className="user-search-button">
        Search
      </button>
      <ul className="user-search-results">
        {results.map((user, index) => (
          <li key={index} onClick={() => onSelectUser(user.username)}>
            {user.username}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default UserSearch;
