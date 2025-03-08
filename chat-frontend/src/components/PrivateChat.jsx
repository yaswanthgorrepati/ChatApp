import React, { useEffect, useState, useRef } from "react";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import "../styles/PrivateChat.css";

const PrivateChat = ({ token, username, recipient: initialRecipient }) => {
  const [stompClient, setStompClient] = useState(null);
  const [recipient, setRecipient] = useState(initialRecipient || "");
  const [message, setMessage] = useState("");
  const [chat, setChat] = useState([]);
  const subscriptionRef = useRef(null);
  const chatWindowRef = useRef(null);

  useEffect(() => {
    if (initialRecipient) setRecipient(initialRecipient);
  }, [initialRecipient]);

  // Fetch conversation history
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
          setChat(data); // Assuming data is an array of ChatMessage objects
        })
        .catch((error) => {
          console.error("Error fetching conversation: ", error);
        });
    }
  }, [username, recipient, token]);

  // Establish WebSocket connection
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
                //show only the messages from the receipient in the chat window
                if (response.from === recipient || response.from === username) {
                  setChat((prevChat) => [
                    ...prevChat,
                    {
                      fromUser: response.from,
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

  // Auto-scroll chat window
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
      {/* <div className="chat-recipient-container">
        <input
          type="text"
          placeholder="Recipient Username"
          value={recipient}
          onChange={(e) => setRecipient(e.target.value)}
          className="chat-input"
        />
      </div> */}
      <p className="chat-with">Chatting with {recipient}</p>
      <div className="chat-window" ref={chatWindowRef}>
        {chat.map((msg, index) => (
          <div key={index} className="chat-message">
            <span className="chat-sender">{msg.fromUser}:</span>
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
