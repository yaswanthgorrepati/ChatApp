import React, { useState, useEffect } from "react";

import PrivateChat from "./PrivateChat";
import UserSearch from "./UserSearch";
import FriendUpdates from "./FriendUpdates";
import Login from "./Login";

import "../styles/PrivateChat.css";

const Chat = () => {
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

  // Handler for friend updates pushed via WebSocket
  const handleFriendUpdate = (update) => {
    setFriends((prevFriends) => {
      // Update the friend entry if exists, or add new one
      const index = prevFriends.findIndex((f) => f.friend === update.friend);
      if (index >= 0) {
        const updatedFriends = [...prevFriends];
        updatedFriends[index].unreadCount = update.unreadCount;
        return updatedFriends;
      } else {
        return [...prevFriends, update];
      }
    });
  };

  return (
    <div className="chat-app-container">
      {!token ? (
        <Login onLogin={handleLogin} />
      ) : (
        <>
          {/* Top area: user search */}
          <div className="search-bar-container">
            <UserSearch
              token={token}
              onSelectUser={(user) => setSelectedUser(user)}
            />
          </div>

          {/* Main chat container: Left sidebar for friends, right for chat */}
          <div className="chat-main">
            <div className="friends-container">
              <h3>Your Chat Friends</h3>
              {friends && friends.length > 0 ? (
                <ul className="friends-list">
                  {friends.map((friend, index) => (
                    <li
                      key={index}
                      onClick={() => setSelectedUser(friend.friend)}
                    >
                      {friend.friend}{" "}
                      {friend.unreadCount > 0 && `(${friend.unreadCount})`}
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="no-chats">No chats found!</p>
              )}
            </div>
            <div className="chat-window-container">
              <FriendUpdates
                token={token}
                username={username}
                onUpdate={handleFriendUpdate}
              />
              {selectedUser ? (
                <PrivateChat
                  token={token}
                  username={username}
                  recipient={selectedUser}
                />
              ) : (
                <p className="no-selection">select user to start chatting!</p>
              )}
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default Chat;
