// Connect to STOMP endpoint and reload page on new events
document.addEventListener('DOMContentLoaded', function() {
    function connectWebSocket() {
        if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') {
            console.warn('WebSocket libraries not loaded');
            return;
        }

        const socket = new SockJS('/ws');
        const stompClient = Stomp.over(socket);

        // Disable debug logging
        stompClient.debug = null;

        stompClient.connect({}, function(frame) {
            console.log('Connected to WebSocket');
            
            // Subscribe to event updates
            stompClient.subscribe('/topic/events', function(message) {
                console.log('Received event update:', message.body);
                // Reload the page to show updated event list
                location.reload();
            });
        }, function(error) {
            console.error('WebSocket connection error:', error);
            // Try to reconnect after 5 seconds
            setTimeout(connectWebSocket, 5000);
        });

        // Handle page unload
        window.addEventListener('beforeunload', function() {
            if (stompClient !== null) {
                stompClient.disconnect();
            }
        });
    }

    // Start the WebSocket connection
    connectWebSocket();
});
