import React, { useState, useEffect, useRef } from 'react';

// Live styling that completely embodies the vibrant, glossy, tech-optimistic Frutiger Aero aesthetic
const aeroStyles = `
  @keyframes aero-float {
    0% { transform: translateY(0px) rotate(0deg); }
    50% { transform: translateY(-15px) rotate(3deg); }
    100% { transform: translateY(0px) rotate(0deg); }
  }

  @keyframes pulse-glow {
    0% { box-shadow: 0 0 10px rgba(0, 229, 255, 0.4), inset 0 0 15px rgba(255,255,255,0.6); }
    50% { box-shadow: 0 0 25px rgba(0, 229, 255, 0.8), inset 0 0 25px rgba(255,255,255,0.9); }
    100% { box-shadow: 0 0 10px rgba(0, 229, 255, 0.4), inset 0 0 15px rgba(255,255,255,0.6); }
  }

  @keyframes bubble-rise {
    0% { transform: translateY(120%) scale(0.8); opacity: 0; }
    10% { opacity: 0.6; }
    90% { opacity: 0.6; }
    100% { transform: translateY(-20%) scale(1.2); opacity: 0; }
  }

  .aero-btn:hover {
    transform: scale(1.03) translateY(-1px);
    box-shadow: 0 8px 20px rgba(2, 163, 254, 0.4), inset 0 4px 10px rgba(255,255,255,1);
    filter: brightness(1.1);
  }

  .aero-btn:active {
    transform: scale(0.98);
    box-shadow: 0 2px 5px rgba(2, 163, 254, 0.2), inset 0 1px 4px rgba(0,0,0,0.1);
  }

  /* Custom high-fidelity scrollbar with glass shine */
  .aero-scroll::-webkit-scrollbar {
    width: 10px;
  }
  .aero-scroll::-webkit-scrollbar-track {
    background: rgba(240, 249, 255, 0.4);
    border-radius: 10px;
  }
  .aero-scroll::-webkit-scrollbar-thumb {
    background: linear-gradient(180deg, rgba(2, 163, 254, 0.7) 0%, rgba(0, 229, 255, 0.7) 100%);
    border: 2px solid rgba(255,255,255,0.8);
    border-radius: 10px;
    box-shadow: inset 0 1px 3px rgba(255,255,255,0.5);
  }
`;

export default function SigeonAeroChat() {
  const [messages, setMessages] = useState([
    {
      id: 1,
      sender: 'Nova',
      text: 'Hello there, explorer! Nova here, your official guide for the Sigeon OS. Welcome to our vibrant virtual assistant portal! 🌟 How may I optimize your workspace today?',
      time: '21:55',
      avatar: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=80&h=80&q=80' // Glass bubble art placeholder
    },
    {
      id: 2,
      sender: 'User',
      text: 'Wow, this interface is super glossy!',
      time: '21:56',
      avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=80&h=80&q=80'
    },
    {
      id: 3,
      sender: 'Caramel',
      text: 'Meow! *Purrs loud* 🐾 Caramel at your service! I custom-coded all these shiny water droplets for you! Are you ready to sync with the Sigeon headquarters established in 1991?',
      time: '21:57',
      avatar: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=80&h=80&q=80'
    }
  ]);

  const [inputText, setInputText] = useState('');
  const [activePersona, setActivePersona] = useState('Nova');
  const chatEndRef = useRef(null);

  // Auto scroll to latest posts
  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = (e) => {
    e.preventDefault();
    if (!inputText.trim()) return;

    const userMsg = {
      id: Date.now(),
      sender: 'User',
      text: inputText,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=80&h=80&q=80'
    };

    setMessages(prev => [...prev, userMsg]);
    setInputText('');

    // Trigger responsive, tech-optimistic virtual Sigeon OS responses!
    setTimeout(() => {
      let responseText = '';
      if (activePersona === 'Caramel') {
        responseText = `Mew! Thank you for that update. ✨ Sigeon OS is running beautifully! Did you know our main Campus Takeover node is fully active in Killeen? Let me blow some bubbles for you! *plays with soap bubbles* 🫧`;
      } else if (activePersona === 'Nova') {
        responseText = `Transmission received! 🌟 Let's align your workstation with Sigeon OS 1991 Core specifications. Try out our shortcut combo Ctrl+Shift+S to open the interactive PlayStation Mall directory!`;
      } else {
        responseText = `Sigeon Virtual Node fully aligned. 🌊 Our tech-optimistic green grids are sustainable, eco-friendly, and running at maximum capability. Let's build a brighter, glossy future together!`;
      }

      setMessages(prev => [...prev, {
        id: Date.now() + 1,
        sender: activePersona,
        text: responseText,
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        avatar: activePersona === 'Caramel' 
          ? 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=80&h=80&q=80'
          : 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=80&h=80&q=80'
      }]);
    }, 1000);
  };

  return (
    <div style={containerStyle}>
      {/* Inject custom animations & styles */}
      <style>{aeroStyles}</style>

      {/* Decorative floating bubbles background (Peak Frutiger Aero) */}
      <div style={bubbleContainerStyle}>
        {[...Array(6)].map((_, i) => (
          <div 
            key={i} 
            style={{
              ...bubbleStyle,
              left: `${15 + i * 16}%`,
              animationDelay: `${i * 2}s`,
              width: `${30 + (i % 3) * 25}px`,
              height: `${30 + (i % 3) * 25}px`,
            }} 
          />
        ))}
      </div>

      {/* Glassmorphic Aero Card Container */}
      <div style={aeroChatCard}>
        {/* Shiny Highlight Top Bar */}
        <div style={aeroGlintBar} />

        {/* Header Segment */}
        <div style={headerStyle}>
          <div style={headerContent}>
            <div style={logoWrapper}>
              {/* Glossy Sphere Accent Icon */}
              <div style={glossSphere} />
              <div>
                <h1 style={titleText}>SIGEON AI ASSISTANT</h1>
                <p style={subtitleText}>Est. 1991 // Powered by Frutiger Aero Technologies</p>
              </div>
            </div>
            {/* Status light */}
            <div style={statusBadge}>
              <span style={statusDot} />
              SYNC ACTIVE
            </div>
          </div>
        </div>

        {/* Persona Select Slider */}
        <div style={personaSelectorContainer}>
          <span style={personaTitle}>SELECT DIRECTORY NODE:</span>
          <div style={personaChipsWrapper}>
            {[
              { id: 'Nova', desc: 'System Guide 🌟' },
              { id: 'Caramel', desc: 'Kitty Mascot 🐾' },
              { id: 'Sigeon AI', desc: 'Core Assistant 🌊' }
            ].map((p) => {
              const isSelected = activePersona === p.id;
              return (
                <button
                  key={p.id}
                  onClick={() => setActivePersona(p.id)}
                  style={{
                    ...personaChipStyle,
                    background: isSelected 
                      ? 'linear-gradient(180deg, #00E5FF 0%, #02A3FE 100%)' 
                      : 'rgba(255, 255, 255, 0.75)',
                    color: isSelected ? '#FFFFFF' : '#0A2540',
                    border: isSelected ? '1px solid #FFFFFF' : '1px solid rgba(2, 163, 254, 0.25)',
                    fontWeight: isSelected ? 'bold' : 'normal',
                    boxShadow: isSelected ? '0 4px 12px rgba(0, 229, 255, 0.4), inset 0 2px 4px #FFFFFF' : 'none'
                  }}
                  className="aero-btn"
                >
                  {p.desc}
                </button>
              );
            })}
          </div>
        </div>

        {/* Scrollable Chat Area */}
        <div className="aero-scroll" style={scrollAreaStyle}>
          <div style={chatHistoryInner}>
            {messages.map((msg, index) => {
              const isUser = msg.sender === 'User';
              return (
                <div 
                  key={msg.id} 
                  style={{
                    ...chatRowStyle,
                    alignSelf: isUser ? 'flex-end' : 'flex-start',
                    flexDirection: isUser ? 'row-reverse' : 'row'
                  }}
                >
                  <img 
                    src={msg.avatar} 
                    alt={msg.sender} 
                    style={avatarStyle} 
                  />
                  
                  <div 
                    style={{
                      ...messageBubbleStyle,
                      background: isUser 
                        ? 'linear-gradient(180deg, rgba(2, 163, 254, 0.85) 0%, rgba(0, 229, 255, 0.85) 100%)'
                        : 'linear-gradient(180deg, rgba(255, 255, 255, 0.9) 0%, rgba(240, 249, 255, 0.9) 100%)',
                      color: isUser ? '#FFFFFF' : '#0A2540',
                      borderRadius: isUser ? '18px 2px 18px 18px' : '2px 18px 18px 18px',
                      border: isUser ? '1px solid rgba(255,255,255,0.4)' : '1px solid rgba(2,163,254,0.15)',
                      boxShadow: isUser 
                        ? '0 4px 10px rgba(2, 163, 254, 0.25), inset 0 2px 4px rgba(255,255,255,0.4)' 
                        : '0 4px 10px rgba(10, 37, 64, 0.05), inset 0 2px 4px rgba(255,255,255,1)'
                    }}
                  >
                    <div style={messageHeader}>
                      <span style={messageSender}>{msg.sender}</span>
                      <span style={messageTime}>{msg.time}</span>
                    </div>
                    <div style={messageText}>{msg.text}</div>
                  </div>
                </div>
              );
            })}
            <div ref={chatEndRef} />
          </div>
        </div>

        {/* Message Input Segment */}
        <form onSubmit={handleSend} style={inputBarContainer}>
          <div style={inputWrapper}>
            <input
              type="text"
              value={inputText}
              onChange={(e) => setInputText(e.target.value)}
              placeholder={`Communicate with ${activePersona}...`}
              style={inputFieldStyle}
            />
            <button 
              type="submit" 
              className="aero-btn" 
              style={sendButtonStyle}
            >
              SEND SYNC
            </button>
          </div>
        </form>

        {/* Footer info/brand bar */}
        <div style={footerStyle}>
          <span>ENERGY EFFICIENCY MODE: HIGH GLOSS (🌿 Eco-Active)</span>
          <span>Sigeon OS Core Interface</span>
        </div>
      </div>
    </div>
  );
}

// Inline styles mirroring the premium glossy Frutiger Aero aesthetic
const containerStyle = {
  position: 'relative',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  width: '100%',
  minHeight: '620px',
  background: 'linear-gradient(135deg, #F0F9FF 0%, #E0F2FE 40%, #D0F7FF 100%)',
  fontFamily: 'system-ui, -apple-system, sans-serif',
  padding: '20px',
  boxSizing: 'border-box',
  overflow: 'hidden'
};

const bubbleContainerStyle = {
  position: 'absolute',
  inset: 0,
  pointerEvents: 'none',
  zIndex: 1
};

const bubbleStyle = {
  position: 'absolute',
  bottom: '-100px',
  borderRadius: '50%',
  background: 'linear-gradient(135deg, rgba(255,255,255,0.7) 0%, rgba(0,229,255,0.2) 100%)',
  boxShadow: 'inset 0 4px 10px rgba(255,255,255,0.6), 0 5px 15px rgba(0, 229, 255, 0.1)',
  border: '1px solid rgba(255,255,255,0.4)',
  animation: 'bubble-rise 12s infinite linear'
};

const aeroChatCard = {
  position: 'relative',
  zIndex: 10,
  width: '100%',
  maxWidth: '520px',
  height: '580px',
  background: 'rgba(255, 255, 255, 0.45)',
  backdropFilter: 'blur(16px)',
  WebkitBackdropFilter: 'blur(16px)',
  borderRadius: '24px',
  border: '2px solid rgba(255, 255, 255, 0.95)',
  boxShadow: '0 20px 50px rgba(10, 37, 64, 0.1), inset 0 0 16px rgba(255, 255, 255, 0.6)',
  display: 'flex',
  flexDirection: 'column',
  overflow: 'hidden'
};

const aeroGlintBar = {
  position: 'absolute',
  top: 0,
  left: 0,
  right: 0,
  height: '50%',
  background: 'linear-gradient(180deg, rgba(255,255,255,0.7) 0%, rgba(255,255,255,0) 100%)',
  pointerEvents: 'none',
  borderTopLeftRadius: '22px',
  borderTopRightRadius: '22px'
};

const headerStyle = {
  background: 'linear-gradient(180deg, rgba(2,163,254,0.85) 0%, rgba(0,229,255,0.85) 100%)',
  padding: '16px 20px',
  borderBottom: '2px solid rgba(255,255,255,0.6)',
  boxShadow: '0 4px 15px rgba(2, 163, 254, 0.15)'
};

const headerContent = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center'
};

const logoWrapper = {
  display: 'flex',
  alignItems: 'center',
  gap: '12px'
};

const glossSphere = {
  width: '32px',
  height: '32px',
  borderRadius: '50%',
  background: 'radial-gradient(circle at 35% 35%, #FFFFFF 0%, #2CCB54 50%, #1B5E20 100%)',
  boxShadow: '0 4px 8px rgba(44, 203, 84, 0.4), inset -2px -2px 6px rgba(0,0,0,0.3)',
  animation: 'aero-float 5s infinite ease-in-out'
};

const titleText = {
  margin: 0,
  fontSize: '16px',
  fontWeight: '800',
  color: '#FFFFFF',
  letterSpacing: '0.5px'
};

const subtitleText = {
  margin: 0,
  fontSize: '10px',
  color: 'rgba(255, 255, 255, 0.85)',
  fontWeight: '600',
  fontFamily: 'monospace'
};

const statusBadge = {
  display: 'flex',
  alignItems: 'center',
  gap: '6px',
  background: 'rgba(255, 255, 255, 0.3)',
  borderRadius: '20px',
  padding: '4px 10px',
  fontSize: '9px',
  fontWeight: '800',
  color: '#FFFFFF',
  border: '1px solid rgba(255, 255, 255, 0.5)'
};

const statusDot = {
  width: '8px',
  height: '8px',
  borderRadius: '50%',
  backgroundColor: '#2CCB54',
  boxShadow: '0 0 8px #2CCB54'
};

const personaSelectorContainer = {
  padding: '12px 16px',
  background: 'rgba(240, 249, 255, 0.6)',
  borderBottom: '1px solid rgba(2, 163, 254, 0.1)',
  display: 'flex',
  flexDirection: 'column',
  gap: '6px'
};

const personaTitle = {
  fontSize: '9px',
  fontWeight: 'bold',
  color: '#0A2540',
  opacity: 0.7,
  letterSpacing: '0.5px'
};

const personaChipsWrapper = {
  display: 'flex',
  gap: '8px'
};

const personaChipStyle = {
  padding: '6px 14px',
  fontSize: '11px',
  borderRadius: '16px',
  outline: 'none',
  cursor: 'pointer',
  transition: 'all 0.25s ease'
};

const scrollAreaStyle = {
  flex: 1,
  overflowY: 'auto',
  padding: '16px 20px',
  display: 'flex',
  flexDirection: 'column'
};

const chatHistoryInner = {
  display: 'flex',
  flexDirection: 'column',
  gap: '16px'
};

const chatRowStyle = {
  display: 'flex',
  gap: '12px',
  maxWidth: '85%'
};

const avatarStyle = {
  width: '36px',
  height: '36px',
  borderRadius: '50%',
  border: '2px solid rgba(255,255,255,1)',
  boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
  objectFit: 'cover'
};

const messageBubbleStyle = {
  padding: '12px 14px',
  position: 'relative',
  transition: 'transform 0.15s ease'
};

const messageHeader = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  gap: '12px',
  marginBottom: '4px'
};

const messageSender = {
  fontSize: '11px',
  fontWeight: 'bold',
  letterSpacing: '0.2px'
};

const messageTime = {
  fontSize: '9px',
  opacity: 0.6
};

const messageText = {
  fontSize: '13px',
  lineHeight: '1.45',
  wordBreak: 'break-word'
};

const inputBarContainer = {
  padding: '14px 18px',
  background: 'rgba(255,255,255,0.7)',
  borderTop: '1px solid rgba(2, 163, 254, 0.15)',
  zIndex: 10
};

const inputWrapper = {
  display: 'flex',
  gap: '10px'
};

const inputFieldStyle = {
  flex: 1,
  border: '2px solid rgba(2, 163, 254, 0.2)',
  borderRadius: '20px',
  padding: '10px 16px',
  fontSize: '13px',
  color: '#0A2540',
  background: 'rgba(255,255,255,0.9)',
  outline: 'none',
  boxShadow: 'inset 0 1px 3px rgba(0,0,0,0.05)',
  transition: 'border-color 0.25s ease'
};

const sendButtonStyle = {
  background: 'linear-gradient(180deg, #2CCB54 0%, #1FAA3E 100%)',
  color: '#FFFFFF',
  border: '1px solid #FFFFFF',
  borderRadius: '20px',
  padding: '0 18px',
  fontSize: '12px',
  fontWeight: '800',
  letterSpacing: '0.5px',
  cursor: 'pointer',
  transition: 'all 0.25s ease',
  boxShadow: '0 4px 12px rgba(44,203,84,0.35), inset 0 2px 4px rgba(255,255,255,0.4)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center'
};

const footerStyle = {
  display: 'flex',
  justifyContent: 'space-between',
  padding: '10px 20px',
  fontSize: '8px',
  fontWeight: 'bold',
  color: 'rgba(10, 37, 64, 0.4)',
  background: 'rgba(240, 249, 255, 0.5)',
  borderTop: '1px solid rgba(255, 255, 255, 0.8)',
  letterSpacing: '0.3px'
};
