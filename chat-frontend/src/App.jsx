import React, { useState, useEffect } from "react";
import Login from "./components/Login";

import PrivateChat from "./components/PrivateChat";
import UserSearch from "./components/UserSearch";

import "./styles/PrivateChat.css";
import "./App.css";

function App() {
  const [token, setToken] = useState("");
  const [username, setUsername] = useState("");
  const [selectedUser, setSelectedUser] = useState("");
  const [friends, setFriends] = useState([]);

  const handleLogin = (jwtToken, user) => {
    setToken(jwtToken);
    setUsername(user);
  };

  const fetchFriends = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/users/friends?username=${encodeURIComponent(
          username
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
        const friendList = await response.json();
        setFriends(friendList);
      } else {
        console.error("Failed to fetch friends list");
      }
    } catch (error) {
      console.error("Error fetching friends:", error);
    }
  };

  useEffect(() => {
    if (username && token) {
      fetchFriends();
    }
  }, [username, token]);

  return (
    <div className="App">
      {!token ? (
        <Login onLogin={handleLogin} />
      ) : (
        <>
          <div className="friends-container">
            <h3>Your Chat Friends</h3>
            {friends && friends.length > 0 ? (
              <ul className="friends-list">
                {friends.map((friend, index) => (
                  <li key={index} onClick={() => setSelectedUser(friend)}>
                    {friend}
                  </li>
                ))}
              </ul>
            ) : (
              <p className="no-chats">No chat friends found!</p>
            )}
          </div>
          <UserSearch
            token={token}
            onSelectUser={(user) => setSelectedUser(user)}
          />
          {selectedUser ? (
            <PrivateChat
              token={token}
              username={username}
              recipient={selectedUser}
            />
          ) : null}
        </>
      )}
    </div>
  );
}

export default App;
