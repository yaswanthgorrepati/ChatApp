import React, { useEffect } from "react";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

const FriendUpdates = ({ token, username, onUpdate }) => {
  useEffect(() => {
    // Create a SockJS connection with the username as a query parameter
    const socket = new SockJS(
      `http://localhost:8080/ws?username=${encodeURIComponent(username)}`
    );
    const client = Stomp.over(socket);

    // Connect and wait for the connection callback before subscribing
    client.connect(
      { Authorization: "Bearer " + token },
      (frame) => {
        console.log("FriendUpdates connected: " + frame);
        // Now that the connection is established, subscribe to friend updates
        client.subscribe("/user/queue/friendUpdates", (msg) => {
          const update = JSON.parse(msg.body);
          onUpdate(update);
        });
      },
      (error) => {
        console.error("FriendUpdates connection error: ", error);
      }
    );

    return () => {
      if (client && client.connected) {
        client.disconnect(() => console.log("FriendUpdates disconnected"));
      }
    };
  }, [token, username, onUpdate]);

  return null; // This component does not render any visible UI.
};

export default FriendUpdates;
