(function () {
	const WIDGET_ID = 'chat-ai-widget';
	const FAB_ID = 'chat-ai-fab';
	const PANEL_ID = 'chat-ai-panel';
    const MODEL = 'gemini-1.5-flash';

	if (document.getElementById(WIDGET_ID)) return;

	function injectStyles() {
		const style = document.createElement('style');
		style.id = 'chat-ai-styles';
		style.textContent = `
			#${FAB_ID}{position:fixed;right:22px;bottom:22px;width:56px;height:56px;border-radius:50%;
				background:linear-gradient(135deg,#6a11cb,#2575fc);color:#fff;display:flex;align-items:center;
				justify-content:center;box-shadow:0 8px 16px rgba(0,0,0,0.2);cursor:pointer;z-index:2147483000;}
			#${FAB_ID}:hover{transform:translateY(-1px);box-shadow:0 10px 18px rgba(0,0,0,.25)}
			#${PANEL_ID}{position:fixed;right:22px;bottom:90px;width:360px;max-width:92vw;height:520px;max-height:80vh;
				border-radius:16px;background:#fff;color:#222;box-shadow:0 16px 40px rgba(0,0,0,.25);display:none;
				flex-direction:column;overflow:hidden;z-index:2147483001;font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif}
			#${PANEL_ID}.dark{background:#1f2937;color:#e5e7eb}
			#${PANEL_ID} .cap{display:flex;align-items:center;justify-content:space-between;padding:10px 12px;border-bottom:1px solid rgba(0,0,0,.06)}
			#${PANEL_ID}.dark .cap{border-color:rgba(255,255,255,.08)}
			#${PANEL_ID} .cap .actions button{background:transparent;border:none;color:inherit;cursor:pointer;padding:6px}
			#${PANEL_ID} .msgs{flex:1;overflow:auto;padding:12px;background:#f8fafc}
			#${PANEL_ID}.dark .msgs{background:#111827}
			#${PANEL_ID} .msg{margin-bottom:10px;display:flex;gap:8px}
			#${PANEL_ID} .msg.user{justify-content:flex-end}
			#${PANEL_ID} .bubble{max-width:78%;padding:10px 12px;border-radius:12px;white-space:pre-wrap;word-break:break-word}
			#${PANEL_ID} .user .bubble{background:#2563eb;color:#fff;border-bottom-right-radius:4px}
			#${PANEL_ID} .ai .bubble{background:#e5e7eb;color:#111827;border-bottom-left-radius:4px}
			#${PANEL_ID}.dark .ai .bubble{background:#374151;color:#e5e7eb}
			#${PANEL_ID} .input{display:flex;gap:8px;padding:10px;border-top:1px solid rgba(0,0,0,.06)}
			#${PANEL_ID}.dark .input{border-color:rgba(255,255,255,.08)}
			#${PANEL_ID} .input input{flex:1;border:1px solid #e5e7eb;border-radius:10px;padding:10px 12px;outline:none}
			#${PANEL_ID}.dark .input input{background:#111827;color:#e5e7eb;border-color:#374151}
			#${PANEL_ID} .input button{background:#2563eb;color:#fff;border:none;border-radius:10px;padding:10px 14px;min-width:70px}
            #${PANEL_ID} .hint{font-size:12px;opacity:.7;margin-left:8px}
		`;
		document.head.appendChild(style);
	}

	function createWidget() {
		const wrap = document.createElement('div');
		wrap.id = WIDGET_ID;
		wrap.innerHTML = `
			<div id="${FAB_ID}" title="ChatAI">
				<svg width="26" height="26" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
					<path d="M12 22C17.523 22 22 17.523 22 12S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10Z" fill="currentColor" opacity=".15"/>
					<path d="M7.5 10.5h9M7.5 13.5h5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
				</svg>
			</div>
			<div id="${PANEL_ID}" aria-live="polite" role="dialog">
                <div class="cap">
                    <strong>ChatAI</strong>
                    <div class="actions">
                        <button id="chat-ai-close" title="Đóng">✖</button>
                    </div>
                </div>
				<div class="msgs" id="chat-ai-msgs"></div>
				<div class="input">
					<input id="chat-ai-input" type="text" placeholder="Nhập câu hỏi...">
					<button id="chat-ai-send">Gửi</button>
				</div>
                <div class="hint">Chat bởi AI Tin nhắn có thể được lưu trữ để cải thiện dịch vụ.</div>
			</div>
		`;
		document.body.appendChild(wrap);
	}

	function togglePanel(show) {
		const panel = document.getElementById(PANEL_ID);
		if (!panel) return;
		panel.style.display = show === undefined ? (panel.style.display === 'none' || panel.style.display === '' ? 'flex' : 'none') : (show ? 'flex' : 'none');
	}

	function appendMessage(role, text) {
		const msgs = document.getElementById('chat-ai-msgs');
		if (!msgs) return;
		const row = document.createElement('div');
		row.className = `msg ${role}`;
		const bubble = document.createElement('div');
		bubble.className = 'bubble';
		bubble.textContent = text;
		row.appendChild(bubble);
		msgs.appendChild(row);
		msgs.scrollTop = msgs.scrollHeight;
	}

    async function callGemini(prompt, history) {
        const res = await fetch('/api/chat-ai/ask', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ prompt, history })
        });
        const data = await res.json().catch(() => ({}));
        if (!res.ok || !data.success) {
            throw new Error(data.message || `ChatAI lỗi: ${res.status}`);
        }
        return data.message || 'Xin lỗi, mình chưa có câu trả lời.';
    }

	    function initBehavior() {
        const fab = document.getElementById(FAB_ID);
        const closeBtn = document.getElementById('chat-ai-close');
        const input = document.getElementById('chat-ai-input');
        const send = document.getElementById('chat-ai-send');

        let history = [];
        let autoGreetTimeout;

		        // Thêm function chào hỏi tự động
        function showAutoGreeting() {
            appendMessage('ai', 
                "Xin chào! Tôi là trợ lý AI của cửa hàng quạt điện. 😊\n\n" +
                "Tôi có thể giúp bạn:\n" +
                "• Tư vấn sản phẩm phù hợp\n" +
                "• Kiểm tra giá và tồn kho\n" +
                "• Thông tin về các loại quạt\n\n" +
                "Bạn cần hỗ trợ gì không ạ?"
            );
        }

        // Khi mở chat panel
        fab.addEventListener('click', () => {
            const wasOpen = document.getElementById(PANEL_ID).style.display === 'flex';
            togglePanel();
            
            // Chỉ set timeout khi mở chat lần đầu
            if (!wasOpen) {
                // Clear timeout cũ nếu có
                if (autoGreetTimeout) {
                    clearTimeout(autoGreetTimeout);
                }
                
                // Set timeout 5 giây để chào
                autoGreetTimeout = setTimeout(() => {
                    // Chỉ chào nếu user chưa nhắn gì và panel vẫn mở
                    const msgs = document.getElementById('chat-ai-msgs');
                    if (msgs && msgs.children.length === 0 && 
                        document.getElementById(PANEL_ID).style.display === 'flex') {
                        showAutoGreeting();
                    }
                }, 5000); // 5 seconds
            }
        });

        // Clear timeout khi đóng panel
        closeBtn.addEventListener('click', () => {
            if (autoGreetTimeout) {
                clearTimeout(autoGreetTimeout);
                autoGreetTimeout = null;
            }
            togglePanel(false);
        });

        // Clear timeout khi user bắt đầu nhập
        input.addEventListener('input', () => {
            if (autoGreetTimeout) {
                clearTimeout(autoGreetTimeout);
                autoGreetTimeout = null;
            }
        });

        // Clear timeout khi user bắt đầu gõ
        input.addEventListener('keydown', () => {
            if (autoGreetTimeout) {
                clearTimeout(autoGreetTimeout);
                autoGreetTimeout = null;
            }
        });

		async function handleSend() {
			const text = input.value.trim();
			if (!text) return;
			input.value = '';
			appendMessage('user', text);
			appendMessage('ai', 'Đang soạn trả lời...');
			const msgs = document.getElementById('chat-ai-msgs');
			const thinking = msgs.querySelector('.msg.ai:last-child .bubble');
			try {
				const reply = await callGemini(text, history);
				if (thinking) thinking.textContent = reply;
				history.push({ role: 'user', text });
				history.push({ role: 'assistant', text: reply });
			} catch (err) {
				if (thinking) thinking.textContent = err.message || 'Lỗi khi gọi Gemini API';
			}
		}

		send.addEventListener('click', handleSend);
		input.addEventListener('keydown', (e) => {
			if (e.key === 'Enter' && !e.shiftKey) {
				e.preventDefault();
				handleSend();
			}
		});
	}

	// Init
	try {
		injectStyles();
		createWidget();
		initBehavior();
	} catch (e) {
		console.error('ChatAI init error:', e);
	}
})();


