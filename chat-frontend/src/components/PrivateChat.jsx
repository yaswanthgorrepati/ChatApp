import React, { useEffect, useState, useRef } from "react";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import "../styles/PrivateChat.css";

const PrivateChat = ({ token, username, recipient: initialRecipient }) => {
  const [stompClient, setStompClient] = useState(null);
  const [message, setMessage] = useState("");
  const [chat, setChat] = useState([]);
  const subscriptionRef = useRef(null);
  const chatWindowRef = useRef(null);
  const [recipient, setRecipient] = useState(initialRecipient || "");

  // Update recipient if the prop changes
  useEffect(() => {
    if (initialRecipient) setRecipient(initialRecipient);
  }, [initialRecipient]);

  // Fetch conversation history when username and recipient are available
  useEffect(() => {
    if (username && recipient) {
      fetch(
        `http://localhost:8080/chat/conversation?user1=${encodeURIComponent(
          username
        )}&user2=${encodeURIComponent(recipient)}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
          },
        }
      )
        .then((response) => response.text())
        .then((text) => (text ? JSON.parse(text) : []))
        .then((data) => {
          setChat(
            data.map((msg) => ({
              from: msg.fromUser,
              content: msg.content,
              timestamp: msg.timestamp,
            }))
          );
        })
        .catch((error) => {
          console.error("Error fetching conversation: ", error);
        });
    }
  }, [username, recipient, token]);

  // Call markRead API when a new message is received from the friend
  useEffect(() => {
    if (username && recipient && chat.length > 0) {
      const lastMsg = chat[chat.length - 1];
      // If the last message is from the friend (i.e. not sent by "Me")
      if (lastMsg.from !== "Me") {
        fetch(
          `http://localhost:8080/chat/markRead?user1=${encodeURIComponent(
            username
          )}&user2=${encodeURIComponent(recipient)}`,
          {
            method: "PUT",
            headers: {
              "Content-Type": "application/json",
              Authorization: "Bearer " + token,
            },
          }
        )
          .then((response) => {
            if (!response.ok) {
              throw new Error("Failed to mark messages as read");
            }
            return response.text();
          })
          .then((data) => {
            console.log("Messages marked as read:", data);
          })
          .catch((error) => {
            console.error("Error marking messages as read:", error);
          });
      }
    }
  }, [chat, username, recipient, token]);

  // Establish WebSocket connection for real-time messaging
  useEffect(() => {
    let client = null;
    try {
      const socket = new SockJS(
        `http://localhost:8080/ws?username=${encodeURIComponent(username)}`
      );
      client = Stomp.over(socket);
      client.connect(
        { Authorization: "Bearer " + token },
        (frame) => {
          console.log("Connected: " + frame);
          if (!subscriptionRef.current) {
            subscriptionRef.current = client.subscribe(
              "/user/queue/messages",
              (msg) => {
                const response = JSON.parse(msg.body);
                if (response.from === username || response.from === recipient) {
                  setChat((prevChat) => [
                    ...prevChat,
                    {
                      from: response.from,
                      content: response.content,
                      timestamp: response.timestamp,
                    },
                  ]);
                }
              }
            );
          }
        },
        (error) => {
          console.error("WebSocket connection error: ", error);
        }
      );
      setStompClient(client);
    } catch (err) {
      console.log(err);
    }
    return () => {
      if (client) {
        try {
          client.disconnect();
        } catch (err) {
          console.log(err);
        }
      }
    };
  }, [username, token]);

  // Auto-scroll chat window when new messages arrive
  useEffect(() => {
    if (chatWindowRef.current) {
      chatWindowRef.current.scrollTop = chatWindowRef.current.scrollHeight;
    }
  }, [chat]);

  const sendMessage = () => {
    if (stompClient && username && recipient && message.trim() !== "") {
      const chatMessage = {
        fromUser: username,
        toUser: recipient,
        content: message,
      };
      stompClient.send("/app/private", {}, JSON.stringify(chatMessage));
      setMessage("");
    }
  };

  const handleKeyPress = (event) => {
    if (event.key === "Enter") sendMessage();
  };

  return (
    <div className="private-chat-container">
      <h2>Real-Time Private Chat</h2>
      <div className="chat-recipient-container">
        <input
          type="text"
          placeholder="Recipient Username"
          value={recipient}
          onChange={(e) => setRecipient(e.target.value)}
          className="chat-input"
        />
      </div>
      <p className="chat-with">Chatting with {recipient}</p>
      <div className="chat-window" ref={chatWindowRef}>
        {chat.map((msg, index) => (
          <div key={index} className="chat-message">
            <span className="chat-sender">{msg.from}:</span>
            <span className="chat-content">{msg.content}</span>
            <span className="chat-timestamp">{msg.timestamp}</span>
          </div>
        ))}
      </div>
      <div className="chat-input-container">
        <input
          type="text"
          placeholder="Type your message"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyPress={handleKeyPress}
          className="chat-message-input"
        />
        <button onClick={sendMessage} className="chat-send-button">
          Send
        </button>
      </div>
    </div>
  );
};

export default PrivateChat;
