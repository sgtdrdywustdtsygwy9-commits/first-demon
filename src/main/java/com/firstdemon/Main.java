package com.firstdemon;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add HelloServlet
        context.addServlet(new ServletHolder(new HelloServlet()), "/hello");

        // Add index servlet
        context.addServlet(new ServletHolder(new IndexServlet()), "/");

        server.start();
        System.out.println("Jetty started on http://localhost:8080/");
        System.out.println("Visit http://localhost:8080/hello for the servlet");
        server.join();
    }

    // Index servlet with dancing character
    public static class IndexServlet extends jakarta.servlet.http.HttpServlet {
        @Override
        protected void doGet(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp)
                throws jakarta.servlet.ServletException, java.io.IOException {
            resp.setContentType("text/html;charset=UTF-8");
            var out = resp.getWriter();
            out.println("""
                <!DOCTYPE html>
                <html lang="zh">
                <head>
                    <meta charset="UTF-8">
                    <title>First Demon</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body {
                            min-height: 100vh;
                            background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
                            display: flex;
                            flex-direction: column;
                            align-items: center;
                            justify-content: center;
                            font-family: 'Segoe UI', sans-serif;
                            overflow: hidden;
                        }
                        h1 {
                            color: #e94560;
                            font-size: 3rem;
                            margin-bottom: 10px;
                            text-shadow: 0 0 20px rgba(233, 69, 96, 0.5);
                        }
                        .subtitle {
                            color: #a8a8a8;
                            margin-bottom: 40px;
                        }
                        a { color: #00d2ff; text-decoration: none; }
                        a:hover { text-decoration: underline; }

                        /* 舞台 */
                        .stage {
                            position: relative;
                            width: 200px;
                            height: 250px;
                            margin: 20px 0;
                        }

                        /* 小人 */
                        .dancer {
                            position: absolute;
                            bottom: 0;
                            left: 50%;
                            transform: translateX(-50%);
                            animation: bounce 0.5s ease-in-out infinite alternate;
                        }

                        /* 头 */
                        .head {
                            width: 40px;
                            height: 40px;
                            background: #ffd93d;
                            border-radius: 50%;
                            margin: 0 auto;
                            position: relative;
                        }
                        .head::before, .head::after {
                            content: '';
                            position: absolute;
                            width: 6px;
                            height: 6px;
                            background: #333;
                            border-radius: 50%;
                            top: 14px;
                        }
                        .head::before { left: 10px; }
                        .head::after { right: 10px; }
                        .mouth {
                            position: absolute;
                            width: 12px;
                            height: 6px;
                            border-bottom: 3px solid #333;
                            border-radius: 0 0 50% 50%;
                            bottom: 10px;
                            left: 50%;
                            transform: translateX(-50%);
                        }

                        /* 身体 */
                        .body {
                            width: 4px;
                            height: 50px;
                            background: #e94560;
                            margin: 0 auto;
                        }

                        /* 手臂 */
                        .arms {
                            position: relative;
                            width: 80px;
                            height: 4px;
                            background: #e94560;
                            margin: 0 auto;
                            animation: arms-wave 0.5s ease-in-out infinite alternate;
                            transform-origin: center;
                        }
                        .arms::before, .arms::after {
                            content: '';
                            position: absolute;
                            width: 35px;
                            height: 4px;
                            background: #e94560;
                            top: 0;
                        }
                        .arms::before {
                            left: -30px;
                            transform: rotate(-45deg);
                            transform-origin: right;
                            animation: left-arm 0.5s ease-in-out infinite alternate;
                        }
                        .arms::after {
                            right: -30px;
                            transform: rotate(45deg);
                            transform-origin: left;
                            animation: right-arm 0.5s ease-in-out infinite alternate;
                        }

                        /* 腿 */
                        .legs {
                            display: flex;
                            justify-content: center;
                            gap: 16px;
                        }
                        .leg {
                            width: 4px;
                            height: 40px;
                            background: #00d2ff;
                            transform-origin: top;
                        }
                        .leg-left { animation: leg-left 0.5s ease-in-out infinite alternate; }
                        .leg-right { animation: leg-right 0.5s ease-in-out infinite alternate; }

                        /* 阴影 */
                        .shadow {
                            width: 60px;
                            height: 10px;
                            background: rgba(0,0,0,0.3);
                            border-radius: 50%;
                            margin: 5px auto 0;
                            animation: shadow-pulse 0.5s ease-in-out infinite alternate;
                        }

                        /* 粒子 */
                        .particles {
                            position: fixed;
                            top: 0; left: 0;
                            width: 100%; height: 100%;
                            pointer-events: none;
                            z-index: -1;
                        }
                        .particle {
                            position: absolute;
                            width: 4px;
                            height: 4px;
                            background: #ffd93d;
                            border-radius: 50%;
                            animation: float 3s ease-in-out infinite;
                        }

                        /* 音符 */
                        .notes {
                            position: absolute;
                            top: 0;
                            left: 50%;
                            transform: translateX(-50%);
                        }
                        .note {
                            position: absolute;
                            font-size: 20px;
                            animation: note-float 1s ease-out forwards;
                            opacity: 0;
                        }

                        /* 动画 */
                        @keyframes bounce {
                            0% { transform: translateX(-50%) translateY(0); }
                            100% { transform: translateX(-50%) translateY(-20px); }
                        }
                        @keyframes left-arm {
                            0% { transform: rotate(-20deg); }
                            100% { transform: rotate(-70deg); }
                        }
                        @keyframes right-arm {
                            0% { transform: rotate(20deg); }
                            100% { transform: rotate(70deg); }
                        }
                        @keyframes leg-left {
                            0% { transform: rotate(20deg); }
                            100% { transform: rotate(-10deg); }
                        }
                        @keyframes leg-right {
                            0% { transform: rotate(-20deg); }
                            100% { transform: rotate(10deg); }
                        }
                        @keyframes shadow-pulse {
                            0% { transform: scaleX(1); opacity: 0.3; }
                            100% { transform: scaleX(0.6); opacity: 0.15; }
                        }
                        @keyframes float {
                            0%, 100% { transform: translateY(0) rotate(0deg); opacity: 0.6; }
                            50% { transform: translateY(-20px) rotate(180deg); opacity: 1; }
                        }
                        @keyframes note-float {
                            0% { transform: translateY(0) rotate(0deg); opacity: 1; }
                            100% { transform: translateY(-60px) rotate(20deg); opacity: 0; }
                        }

                        .controls {
                            margin-top: 30px;
                            display: flex;
                            gap: 15px;
                        }
                        .btn {
                            padding: 10px 25px;
                            border: none;
                            border-radius: 25px;
                            cursor: pointer;
                            font-size: 1rem;
                            transition: all 0.3s;
                        }
                        .btn-primary {
                            background: #e94560;
                            color: white;
                        }
                        .btn-primary:hover {
                            background: #c73e54;
                            transform: scale(1.05);
                        }
                        .btn-secondary {
                            background: transparent;
                            color: #00d2ff;
                            border: 2px solid #00d2ff;
                        }
                        .btn-secondary:hover {
                            background: #00d2ff;
                            color: #1a1a2e;
                        }
                    </style>
                </head>
                <body>
                    <div class="particles" id="particles"></div>

                    <h1>🕺 First Demon</h1>
                    <p class="subtitle">欢迎来到我的 Java Web 项目</p>

                    <div class="stage">
                        <div class="notes" id="notes"></div>
                        <div class="dancer" id="dancer">
                            <div class="head">
                                <div class="mouth"></div>
                            </div>
                            <div class="arms"></div>
                            <div class="body"></div>
                            <div class="legs">
                                <div class="leg leg-left"></div>
                                <div class="leg leg-right"></div>
                            </div>
                        </div>
                        <div class="shadow"></div>
                    </div>

                    <div class="controls">
                        <button class="btn btn-primary" onclick="toggleDance()">⏯ 暂停/继续</button>
                        <button class="btn btn-secondary" onclick="spawnNotes()">🎵 音符</button>
                        <a href="/hello" class="btn btn-secondary">👋 Hello Servlet</a>
                    </div>

                    <script>
                        // 生成背景粒子
                        const particles = document.getElementById('particles');
                        for (let i = 0; i < 30; i++) {
                            const p = document.createElement('div');
                            p.className = 'particle';
                            p.style.left = Math.random() * 100 + '%';
                            p.style.top = Math.random() * 100 + '%';
                            p.style.animationDelay = Math.random() * 3 + 's';
                            p.style.animationDuration = (2 + Math.random() * 2) + 's';
                            const colors = ['#ffd93d', '#e94560', '#00d2ff', '#6c5ce7'];
                            p.style.background = colors[Math.floor(Math.random() * colors.length)];
                            particles.appendChild(p);
                        }

                        // 暂停/继续跳舞
                        let dancing = true;
                        function toggleDance() {
                            dancing = !dancing;
                            const dancer = document.getElementById('dancer');
                            dancer.style.animationPlayState = dancing ? 'running' : 'paused';
                            document.querySelectorAll('.arms, .leg-left, .leg-right, .shadow').forEach(el => {
                                el.style.animationPlayState = dancing ? 'running' : 'paused';
                            });
                        }

                        // 飘出音符
                        const musicNotes = ['🎵', '🎶', '♪', '♫', '🎸', '🎹'];
                        function spawnNotes() {
                            const notes = document.getElementById('notes');
                            for (let i = 0; i < 5; i++) {
                                setTimeout(() => {
                                    const note = document.createElement('span');
                                    note.className = 'note';
                                    note.textContent = musicNotes[Math.floor(Math.random() * musicNotes.length)];
                                    note.style.left = (Math.random() * 100 - 50) + 'px';
                                    notes.appendChild(note);
                                    setTimeout(() => note.remove(), 1000);
                                }, i * 150);
                            }
                        }

                        // 自动飘音符
                        setInterval(spawnNotes, 3000);
                    </script>
                </body>
                </html>
                """);
        }
    }
}
