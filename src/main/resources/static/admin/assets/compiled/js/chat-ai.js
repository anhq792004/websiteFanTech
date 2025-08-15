(function () {
	const WIDGET_ID = 'chat-ai-widget';
	const FAB_ID = 'chat-ai-fab';
	const PANEL_ID = 'chat-ai-panel';
	const SUGGESTIONS_ID = 'chat-ai-suggestions';
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
			#${SUGGESTIONS_ID}{position:fixed;right:88px;bottom:35px;max-width:280px;z-index:2147482999;
				background:#fff;border-radius:12px;box-shadow:0 8px 24px rgba(0,0,0,0.15);padding:16px;
				border:1px solid #e1e5e9;display:none;animation:slideInRight 0.3s ease;}
			#${SUGGESTIONS_ID} .suggestion-header{color:#1976d2;font-weight:600;font-size:14px;margin-bottom:8px;
				display:flex;align-items:center;gap:6px;}
			#${SUGGESTIONS_ID} .suggestion-header::before{content:'🤖';}
			#${SUGGESTIONS_ID} .suggestion-item{background:#f8f9fa;border:1px solid #dee2e6;border-radius:8px;
				padding:8px 12px;margin:4px 0;cursor:pointer;font-size:13px;color:#495057;
				transition:all 0.2s ease;display:block;text-align:left;}
			#${SUGGESTIONS_ID} .suggestion-item:hover{background:#e3f2fd;border-color:#2196f3;color:#1976d2;transform:translateX(-2px);}
			@keyframes slideInRight{from{opacity:0;transform:translateX(20px);}to{opacity:1;transform:translateX(0);}}
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
			<div id="${SUGGESTIONS_ID}">
				<div class="suggestion-header">Em là trợ lý ảo của Quạt FanTech</div>
				<button class="suggestion-item" data-query="Quạt mát lạnh giá sốc">Quạt mát lạnh giá sốc</button>
				<button class="suggestion-item" data-query="Quạt cầm tay giá tốt">Quạt cầm tay giá tốt</button>
				<button class="suggestion-item" data-query="Quạt mát giảm siêu to">Quạt mát giảm siêu to</button>
			</div>
			<div id="${PANEL_ID}" aria-live="polite" role="dialog">
                <div class="cap">
                    <strong>Trợ lý AI</strong>
                    <div class="actions">
                        <button id="chat-ai-close" title="Đóng">✖</button>
                    </div>
                </div>
				<div class="msgs" id="chat-ai-msgs"></div>
				<div class="input">
					<input id="chat-ai-input" type="text" placeholder="Nhập câu hỏi...">
					<button id="chat-ai-send">Gửi</button>
				</div>
                <div class="hint">Chat bởi trợ lý AI Tin nhắn có thể được lưu trữ để cải thiện dịch vụ.</div>
			</div>
		`;
		document.body.appendChild(wrap);
	}

	function togglePanel(show) {
		const panel = document.getElementById(PANEL_ID);
		if (!panel) return;
		panel.style.display = show === undefined ? (panel.style.display === 'none' || panel.style.display === '' ? 'flex' : 'none') : (show ? 'flex' : 'none');
	}

	function appendMessage(role, text, saveToHistory = true) {
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

    async function callGemini(prompt, history, retries = 3) {
        for (let i = 0; i < retries; i++) {
            try {
                const res = await fetch('/api/chat-ai/ask', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ prompt, history })
                });
                const data = await res.json().catch(() => ({}));
                if (!res.ok || !data.success) {
                    // Nếu lỗi 503 (overloaded) và còn retry, thử lại sau delay
                    if (res.status === 503 && i < retries - 1) {
                        await new Promise(resolve => setTimeout(resolve, (i + 1) * 2000)); // 2s, 4s, 6s
                        continue;
                    }
                    throw new Error(data.message || `ChatAI lỗi: ${res.status}`);
                }
                return data.message || 'Xin lỗi, mình chưa có câu trả lời.';
            } catch (error) {
                if (i === retries - 1) throw error; // Lỗi cuối cùng
                await new Promise(resolve => setTimeout(resolve, (i + 1) * 1000)); // Delay trước retry
            }
        }
    }

	    function initBehavior() {
        const fab = document.getElementById(FAB_ID);
        const closeBtn = document.getElementById('chat-ai-close');
        const input = document.getElementById('chat-ai-input');
        const send = document.getElementById('chat-ai-send');

        let history = [];
        let autoGreetTimeout;
        let suggestionsTimeout;

        // Cookie functions for chat history
        function getChatHistory() {
            const cookie = document.cookie.split(';').find(c => c.trim().startsWith('chatai_history='));
            if (cookie) {
                try {
                    return JSON.parse(decodeURIComponent(cookie.split('=')[1]));
                } catch (e) {
                    console.warn('Invalid chat history cookie:', e);
                }
            }
            return [];
        }

        function saveChatHistory() {
            const expiryDate = new Date();
            expiryDate.setDate(expiryDate.getDate() + 30);
            document.cookie = `chatai_history=${encodeURIComponent(JSON.stringify(history))}; expires=${expiryDate.toUTCString()}; path=/; SameSite=Lax`;
        }

        function clearChatHistory() {
            history = [];
            document.cookie = 'chatai_history=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
            const messagesDiv = document.getElementById('chat-ai-msgs');
            if (messagesDiv) {
                messagesDiv.innerHTML = '';
            }
        }

        function loadChatHistory() {
            history = getChatHistory();
            const messagesDiv = document.getElementById('chat-ai-msgs');
            if (messagesDiv && history.length > 0) {
                messagesDiv.innerHTML = '';
                history.forEach(msg => {
                    appendMessage(msg.role, msg.text, false); // false = don't save to history again
                });
            }
        }

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
            hideSuggestions(); // Ẩn suggestions khi mở chat
            
            // Clear suggestions timeout
            if (suggestionsTimeout) {
                clearTimeout(suggestionsTimeout);
                suggestionsTimeout = null;
            }
            
            // Load lịch sử khi mở panel lần đầu
            if (!wasOpen) {
                loadChatHistory();
                
                // Chỉ set timeout auto greeting nếu chưa có lịch sử chat
                if (history.length === 0) {
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
			
			// Clear auto greeting timeout khi user bắt đầu chat
			if (autoGreetTimeout) {
				clearTimeout(autoGreetTimeout);
				autoGreetTimeout = null;
			}
			
			appendMessage('user', text);
			appendMessage('ai', 'Đang soạn trả lời...');
			const msgs = document.getElementById('chat-ai-msgs');
			const thinking = msgs.querySelector('.msg.ai:last-child .bubble');
			try {
				const reply = await callGemini(text, history);
				if (thinking) thinking.textContent = reply;
				
				// Save to history and cookie
				history.push({ role: 'user', text });
				history.push({ role: 'assistant', text: reply });
				saveChatHistory();
			} catch (err) {
				const errorMsg = err.message || 'Lỗi khi gọi Gemini API';
				if (thinking) {
					if (errorMsg.includes('overloaded') || errorMsg.includes('503')) {
						thinking.textContent = '🤖 AI hiện đang quá tải!\n\n💡 Vui lòng:\n• Chờ 10-30 giây rồi thử lại\n• Hoặc hỏi câu ngắn gọn hơn\n\nCảm ơn bạn thông cảm! 😊';
					} else if (errorMsg.includes('401') || errorMsg.includes('unauthorized') || errorMsg.includes('API key')) {
						thinking.textContent = '🔑 Lỗi API key!\n\nVui lòng liên hệ admin để:\n• Kiểm tra cấu hình Gemini API\n• Đảm bảo API key còn hạn sử dụng';
					} else if (errorMsg.includes('429') || errorMsg.includes('quota')) {
						thinking.textContent = '⏰ Đã vượt quá giới hạn!\n\nAPI đã đạt quota tối đa hôm nay.\nVui lòng thử lại vào ngày mai! 📅';
					} else {
						thinking.textContent = '❌ Có lỗi xảy ra!\n\n🔄 Vui lòng thử lại sau.\nNếu lỗi tiếp tục, hãy liên hệ admin.';
					}
				}
				console.error('ChatAI Error:', err);
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

	// Global function để xóa lịch sử chat (gọi khi logout)
	window.clearChatAIHistory = function() {
		document.cookie = 'chatai_history=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
		const messagesDiv = document.getElementById('chat-ai-msgs');
		if (messagesDiv) {
			messagesDiv.innerHTML = '';
		}
		console.log('ChatAI history cleared');
	};

	// Suggestions functions
	function showSuggestions() {
		const suggestions = document.getElementById(SUGGESTIONS_ID);
		if (suggestions) {
			suggestions.style.display = 'block';
		}
	}

	function hideSuggestions() {
		const suggestions = document.getElementById(SUGGESTIONS_ID);
		if (suggestions) {
			suggestions.style.display = 'none';
		}
	}

	function handleSuggestionClick(query) {
		// Ẩn suggestions
		hideSuggestions();
		
		// Clear suggestions timeout
		if (suggestionsTimeout) {
			clearTimeout(suggestionsTimeout);
			suggestionsTimeout = null;
		}
		
		// Mở chat nếu chưa mở
		const panel = document.getElementById(PANEL_ID);
		if (panel.style.display !== 'flex') {
			// Click vào FAB để mở chat (sẽ trigger load history)
			const fab = document.getElementById(FAB_ID);
			if (fab) {
				fab.click();
			}
		}
		
		// Tự động gửi query vào chat
		setTimeout(() => {
			const input = document.getElementById('chat-ai-input');
			if (input) {
				input.value = query;
				// Trigger send button click
				const sendBtn = document.getElementById('chat-ai-send');
				if (sendBtn) {
					sendBtn.click();
				}
			}
		}, 200);
	}

	// Init
	try {
		injectStyles();
		createWidget();
		initBehavior();
		
		// Add suggestion click handlers
		document.querySelectorAll(`#${SUGGESTIONS_ID} .suggestion-item`).forEach(btn => {
			btn.addEventListener('click', (e) => {
				const query = e.target.getAttribute('data-query');
				if (query) {
					handleSuggestionClick(query);
				}
			});
		});
		
		// Show suggestions after page load (delay 3s)
		setTimeout(() => {
			showSuggestions();
			// Auto hide after 10s if not interacted
			suggestionsTimeout = setTimeout(() => {
				hideSuggestions();
			}, 10000);
		}, 3000);
		
	} catch (e) {
		console.error('ChatAI init error:', e);
	}
})();


