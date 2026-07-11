package com.firstdemon;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server();

        // 监听所有网络接口，允许局域网访问
        ServerConnector connector = new ServerConnector(server);
        connector.setHost("0.0.0.0");
        connector.setPort(8080);
        server.addConnector(connector);

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

    // Index servlet with interactive dancing character
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
                    <title>First Demon - 跳舞小人互动游戏</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body {
                            min-height: 100vh;
                            background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
                            font-family: 'Segoe UI', sans-serif;
                            overflow: hidden;
                            cursor: crosshair;
                        }

                        /* 顶部信息栏 */
                        .hud {
                            position: fixed;
                            top: 20px;
                            left: 50%;
                            transform: translateX(-50%);
                            display: flex;
                            gap: 30px;
                            z-index: 100;
                        }
                        .stat {
                            background: rgba(0,0,0,0.5);
                            padding: 10px 20px;
                            border-radius: 25px;
                            color: white;
                            font-size: 1.1rem;
                            backdrop-filter: blur(10px);
                        }
                        .stat span { color: #ffd93d; font-weight: bold; }

                        /* 标题 */
                        h1 {
                            position: fixed;
                            top: 80px;
                            left: 50%;
                            transform: translateX(-50%);
                            color: #e94560;
                            font-size: 2.5rem;
                            text-shadow: 0 0 20px rgba(233, 69, 96, 0.5);
                            z-index: 100;
                        }

                        /* 小人容器 */
                        #dancer-container {
                            position: fixed;
                            transition: left 0.3s ease, top 0.3s ease;
                            z-index: 10;
                            cursor: pointer;
                        }

                        /* 小人 */
                        .dancer {
                            position: relative;
                            transform-origin: bottom center;
                        }
                        .dancer.jumping {
                            animation: jump 0.6s ease-out;
                        }
                        .dancer.spinning {
                            animation: spin 0.8s ease-in-out;
                        }
                        .dancer.dizzy {
                            animation: dizzy 0.5s ease-in-out;
                        }

                        /* 头 */
                        .head {
                            width: 50px;
                            height: 50px;
                            background: #ffd93d;
                            border-radius: 50%;
                            margin: 0 auto;
                            position: relative;
                            transition: transform 0.2s;
                        }
                        .eyes {
                            position: absolute;
                            top: 18px;
                            left: 50%;
                            transform: translateX(-50%);
                            display: flex;
                            gap: 12px;
                        }
                        .eye {
                            width: 8px;
                            height: 8px;
                            background: #333;
                            border-radius: 50%;
                            transition: all 0.2s;
                        }
                        .dancer.happy .eye {
                            height: 4px;
                            border-radius: 0 0 50% 50%;
                            transform: translateY(2px);
                        }
                        .dancer.surprised .eye {
                            width: 12px;
                            height: 12px;
                        }
                        .mouth {
                            position: absolute;
                            bottom: 12px;
                            left: 50%;
                            transform: translateX(-50%);
                            width: 15px;
                            height: 8px;
                            border-bottom: 3px solid #333;
                            border-radius: 0 0 50% 50%;
                            transition: all 0.2s;
                        }
                        .dancer.happy .mouth {
                            width: 20px;
                            height: 10px;
                            border-bottom-width: 4px;
                        }
                        .dancer.surprised .mouth {
                            width: 10px;
                            height: 10px;
                            border: 3px solid #333;
                            border-radius: 50%;
                        }

                        /* 身体 */
                        .body-part {
                            width: 4px;
                            height: 55px;
                            background: #e94560;
                            margin: 0 auto;
                        }

                        /* 手臂 */
                        .arms {
                            position: relative;
                            width: 100px;
                            height: 4px;
                            background: #e94560;
                            margin: 0 auto;
                            transform-origin: center;
                        }
                        .arm {
                            position: absolute;
                            width: 40px;
                            height: 4px;
                            background: #e94560;
                            top: 0;
                            transform-origin: right;
                            transition: transform 0.2s;
                        }
                        .arm-left {
                            left: -35px;
                            transform: rotate(-45deg);
                        }
                        .arm-right {
                            right: -35px;
                            left: auto;
                            transform-origin: left;
                            transform: rotate(45deg);
                        }
                        .dancer.waving .arm-left {
                            animation: wave-left 0.3s ease-in-out infinite alternate;
                        }
                        .dancer.waving .arm-right {
                            animation: wave-right 0.3s ease-in-out infinite alternate;
                        }

                        /* 腿 */
                        .legs {
                            display: flex;
                            justify-content: center;
                            gap: 20px;
                        }
                        .leg {
                            width: 4px;
                            height: 45px;
                            background: #00d2ff;
                            transform-origin: top;
                            transition: transform 0.2s;
                        }
                        .dancer.running .leg-left {
                            animation: run-left 0.2s ease-in-out infinite alternate;
                        }
                        .dancer.running .leg-right {
                            animation: run-right 0.2s ease-in-out infinite alternate;
                        }

                        /* 阴影 */
                        .shadow {
                            width: 70px;
                            height: 12px;
                            background: rgba(0,0,0,0.3);
                            border-radius: 50%;
                            margin: 5px auto 0;
                            transition: all 0.3s;
                        }
                        .dancer.jumping ~ .shadow {
                            transform: scaleX(0.5);
                            opacity: 0.15;
                        }

                        /* 粒子效果 */
                        .click-particle {
                            position: fixed;
                            pointer-events: none;
                            z-index: 1000;
                            font-size: 20px;
                            animation: particle-fly 1s ease-out forwards;
                        }

                        /* 得分弹出 */
                        .score-popup {
                            position: fixed;
                            pointer-events: none;
                            z-index: 1000;
                            color: #ffd93d;
                            font-size: 1.5rem;
                            font-weight: bold;
                            animation: score-fly 1s ease-out forwards;
                        }

                        /* 奖励物品 */
                        .reward {
                            position: fixed;
                            font-size: 30px;
                            cursor: pointer;
                            z-index: 50;
                            animation: reward-float 3s ease-in-out infinite;
                            transition: transform 0.2s;
                        }
                        .reward:hover {
                            transform: scale(1.3);
                        }
                        .reward.collected {
                            animation: reward-collect 0.5s ease-out forwards;
                        }

                        /* 连击提示 */
                        .combo {
                            position: fixed;
                            top: 50%;
                            left: 50%;
                            transform: translate(-50%, -50%);
                            font-size: 3rem;
                            color: #ffd93d;
                            text-shadow: 0 0 30px rgba(255, 217, 61, 0.8);
                            pointer-events: none;
                            z-index: 200;
                            animation: combo-show 1s ease-out forwards;
                        }

                        /* 音符 */
                        .music-note {
                            position: fixed;
                            font-size: 24px;
                            pointer-events: none;
                            z-index: 50;
                            animation: note-dance 2s ease-out forwards;
                        }

                        /* 动画定义 */
                        @keyframes jump {
                            0% { transform: translateY(0); }
                            40% { transform: translateY(-120px); }
                            50% { transform: translateY(-130px); }
                            100% { transform: translateY(0); }
                        }
                        @keyframes spin {
                            0% { transform: rotate(0deg); }
                            100% { transform: rotate(720deg); }
                        }
                        @keyframes dizzy {
                            0%, 100% { transform: rotate(0deg); }
                            25% { transform: rotate(15deg); }
                            75% { transform: rotate(-15deg); }
                        }
                        @keyframes wave-left {
                            0% { transform: rotate(-20deg); }
                            100% { transform: rotate(-80deg); }
                        }
                        @keyframes wave-right {
                            0% { transform: rotate(20deg); }
                            100% { transform: rotate(80deg); }
                        }
                        @keyframes run-left {
                            0% { transform: rotate(30deg); }
                            100% { transform: rotate(-30deg); }
                        }
                        @keyframes run-right {
                            0% { transform: rotate(-30deg); }
                            100% { transform: rotate(30deg); }
                        }
                        @keyframes particle-fly {
                            0% { transform: translate(0, 0) scale(1); opacity: 1; }
                            100% { transform: translate(var(--tx), var(--ty)) scale(0); opacity: 0; }
                        }
                        @keyframes score-fly {
                            0% { transform: translateY(0) scale(1); opacity: 1; }
                            100% { transform: translateY(-80px) scale(1.5); opacity: 0; }
                        }
                        @keyframes reward-float {
                            0%, 100% { transform: translateY(0); }
                            50% { transform: translateY(-15px); }
                        }
                        @keyframes reward-collect {
                            0% { transform: scale(1); opacity: 1; }
                            100% { transform: scale(2); opacity: 0; }
                        }
                        @keyframes combo-show {
                            0% { transform: translate(-50%, -50%) scale(0.5); opacity: 0; }
                            50% { transform: translate(-50%, -50%) scale(1.2); opacity: 1; }
                            100% { transform: translate(-50%, -50%) scale(1); opacity: 0; }
                        }
                        @keyframes note-dance {
                            0% { transform: translate(0, 0) rotate(0deg); opacity: 1; }
                            100% { transform: translate(var(--tx), -200px) rotate(360deg); opacity: 0; }
                        }

                        /* 控制按钮 */
                        .controls {
                            position: fixed;
                            bottom: 30px;
                            left: 50%;
                            transform: translateX(-50%);
                            display: flex;
                            gap: 15px;
                            z-index: 100;
                        }
                        .btn {
                            padding: 12px 25px;
                            border: none;
                            border-radius: 25px;
                            cursor: pointer;
                            font-size: 1rem;
                            transition: all 0.3s;
                            backdrop-filter: blur(10px);
                        }
                        .btn-primary {
                            background: rgba(233, 69, 96, 0.8);
                            color: white;
                        }
                        .btn-primary:hover {
                            background: #e94560;
                            transform: scale(1.05);
                        }
                        .btn-secondary {
                            background: rgba(0, 210, 255, 0.2);
                            color: #00d2ff;
                            border: 2px solid #00d2ff;
                        }
                        .btn-secondary:hover {
                            background: rgba(0, 210, 255, 0.4);
                        }

                        /* 提示文字 */
                        .hint {
                            position: fixed;
                            bottom: 80px;
                            left: 50%;
                            transform: translateX(-50%);
                            color: rgba(255,255,255,0.5);
                            font-size: 0.9rem;
                            z-index: 100;
                        }

                        /* 背景星星 */
                        .stars {
                            position: fixed;
                            top: 0; left: 0;
                            width: 100%; height: 100%;
                            pointer-events: none;
                            z-index: 0;
                        }
                        .star {
                            position: absolute;
                            width: 2px;
                            height: 2px;
                            background: white;
                            border-radius: 50%;
                            animation: twinkle 2s ease-in-out infinite;
                        }
                        @keyframes twinkle {
                            0%, 100% { opacity: 0.3; }
                            50% { opacity: 1; }
                        }
                    </style>
                </head>
                <body>
                    <div class="stars" id="stars"></div>

                    <div class="hud">
                        <div class="stat">🎯 得分: <span id="score">0</span></div>
                        <div class="stat">🔥 连击: <span id="combo">0</span></div>
                        <div class="stat">⭐ 最高: <span id="best">0</span></div>
                    </div>

                    <h1>🕺 点我跳舞!</h1>

                    <div id="dancer-container">
                        <div class="dancer" id="dancer">
                            <div class="head">
                                <div class="eyes">
                                    <div class="eye" id="eye-left"></div>
                                    <div class="eye" id="eye-right"></div>
                                </div>
                                <div class="mouth" id="mouth"></div>
                            </div>
                            <div class="arms">
                                <div class="arm arm-left"></div>
                                <div class="arm arm-right"></div>
                            </div>
                            <div class="body-part"></div>
                            <div class="legs">
                                <div class="leg leg-left"></div>
                                <div class="leg leg-right"></div>
                            </div>
                        </div>
                        <div class="shadow"></div>
                    </div>

                    <p class="hint">💡 点击小人跳跃 | 双击旋转 | 移动鼠标追小人 | 收集礼物得分</p>

                    <div class="controls">
                        <button class="btn btn-primary" onclick="makeDance('wave')">👋 招手</button>
                        <button class="btn btn-secondary" onclick="makeDance('spin')">🔄 旋转</button>
                        <button class="btn btn-secondary" onclick="spawnRewards()">🎁 刷礼物</button>
                        <a href="/hello" class="btn btn-secondary">🔗 Hello</a>
                    </div>

                    <script>
                        // 游戏状态
                        let score = 0;
                        let combo = 0;
                        let bestScore = parseInt(localStorage.getItem('bestScore') || '0');
                        let lastClickTime = 0;
                        let dancerX = window.innerWidth / 2;
                        let dancerY = window.innerHeight / 2;
                        let isJumping = false;
                        let isFollowing = false;

                        const dancerContainer = document.getElementById('dancer-container');
                        const dancer = document.getElementById('dancer');
                        const scoreEl = document.getElementById('score');
                        const comboEl = document.getElementById('combo');
                        const bestEl = document.getElementById('best');

                        bestEl.textContent = bestScore;

                        // 初始化小人位置
                        dancerContainer.style.left = dancerX + 'px';
                        dancerContainer.style.top = dancerY + 'px';

                        // 生成背景星星
                        const starsEl = document.getElementById('stars');
                        for (let i = 0; i < 100; i++) {
                            const star = document.createElement('div');
                            star.className = 'star';
                            star.style.left = Math.random() * 100 + '%';
                            star.style.top = Math.random() * 100 + '%';
                            star.style.animationDelay = Math.random() * 2 + 's';
                            star.style.animationDuration = (1 + Math.random() * 2) + 's';
                            starsEl.appendChild(star);
                        }

                        // 鼠标移动 - 小人看向鼠标
                        document.addEventListener('mousemove', (e) => {
                            const eyeLeft = document.getElementById('eye-left');
                            const eyeRight = document.getElementById('eye-right');
                            const head = dancer.querySelector('.head');
                            const rect = head.getBoundingClientRect();
                            const headCenterX = rect.left + rect.width / 2;
                            const headCenterY = rect.top + rect.height / 2;

                            const angle = Math.atan2(e.clientY - headCenterY, e.clientX - headCenterX);
                            const distance = Math.min(3, Math.hypot(e.clientX - headCenterX, e.clientY - headCenterY) / 50);

                            const eyeX = Math.cos(angle) * distance;
                            const eyeY = Math.sin(angle) * distance;

                            eyeLeft.style.transform = `translate(${eyeX}px, ${eyeY}px)`;
                            eyeRight.style.transform = `translate(${eyeX}px, ${eyeY}px)`;

                            // 小人慢慢转向鼠标
                            if (!isJumping) {
                                const dancerRect = dancerContainer.getBoundingClientRect();
                                const dancerCenterX = dancerRect.left + dancerRect.width / 2;
                                if (e.clientX < dancerCenterX - 50) {
                                    dancerContainer.style.transform = 'scaleX(-1)';
                                } else if (e.clientX > dancerCenterX + 50) {
                                    dancerContainer.style.transform = 'scaleX(1)';
                                }
                            }
                        });

                        // 点击小人 - 跳跃得分
                        dancerContainer.addEventListener('click', (e) => {
                            e.stopPropagation();
                            if (isJumping) return;

                            isJumping = true;
                            dancer.classList.add('jumping');

                            // 连击系统
                            const now = Date.now();
                            if (now - lastClickTime < 800) {
                                combo++;
                                if (combo >= 5) {
                                    showCombo(combo);
                                }
                            } else {
                                combo = 1;
                            }
                            lastClickTime = now;

                            // 计算得分
                            const points = 10 * combo;
                            addScore(points, e.clientX, e.clientY);

                            // 粒子效果
                            spawnClickParticles(e.clientX, e.clientY);

                            // 飘音符
                            spawnMusicNotes(e.clientX, e.clientY);

                            setTimeout(() => {
                                dancer.classList.remove('jumping');
                                isJumping = false;
                            }, 600);
                        });

                        // 双击旋转
                        dancerContainer.addEventListener('dblclick', (e) => {
                            e.stopPropagation();
                            dancer.classList.add('spinning');
                            addScore(50, e.clientX, e.clientY);
                            spawnClickParticles(e.clientX, e.clientY, '🌟');
                            setTimeout(() => dancer.classList.remove('spinning'), 800);
                        });

                        // 点击空白区域 - 小人跑过去
                        document.addEventListener('click', (e) => {
                            if (e.target.closest('.controls') || e.target.closest('.reward')) return;

                            dancerX = e.clientX;
                            dancerY = e.clientY;
                            dancerContainer.style.left = dancerX + 'px';
                            dancerContainer.style.top = dancerY + 'px';

                            dancer.classList.add('running');
                            setTimeout(() => dancer.classList.remove('running'), 500);
                        });

                        // 添加得分
                        function addScore(points, x, y) {
                            score += points;
                            scoreEl.textContent = score;

                            if (score > bestScore) {
                                bestScore = score;
                                bestEl.textContent = bestScore;
                                localStorage.setItem('bestScore', bestScore);
                            }

                            // 显示得分弹出
                            const popup = document.createElement('div');
                            popup.className = 'score-popup';
                            popup.textContent = '+' + points;
                            popup.style.left = x + 'px';
                            popup.style.top = y + 'px';
                            document.body.appendChild(popup);
                            setTimeout(() => popup.remove(), 1000);
                        }

                        // 点击粒子效果
                        function spawnClickParticles(x, y, emoji = '✨') {
                            for (let i = 0; i < 8; i++) {
                                const particle = document.createElement('div');
                                particle.className = 'click-particle';
                                particle.textContent = emoji;
                                particle.style.left = x + 'px';
                                particle.style.top = y + 'px';

                                const angle = (Math.PI * 2 / 8) * i;
                                const distance = 50 + Math.random() * 50;
                                particle.style.setProperty('--tx', Math.cos(angle) * distance + 'px');
                                particle.style.setProperty('--ty', Math.sin(angle) * distance + 'px');

                                document.body.appendChild(particle);
                                setTimeout(() => particle.remove(), 1000);
                            }
                        }

                        // 飘出音符
                        function spawnMusicNotes(x, y) {
                            const notes = ['🎵', '🎶', '♪', '♫', '🎸', '🎹', '🎷', '🥁'];
                            for (let i = 0; i < 3; i++) {
                                const note = document.createElement('div');
                                note.className = 'music-note';
                                note.textContent = notes[Math.floor(Math.random() * notes.length)];
                                note.style.left = x + 'px';
                                note.style.top = y + 'px';
                                note.style.setProperty('--tx', (Math.random() - 0.5) * 100 + 'px');
                                document.body.appendChild(note);
                                setTimeout(() => note.remove(), 2000);
                            }
                        }

                        // 显示连击
                        function showCombo(count) {
                            const combo = document.createElement('div');
                            combo.className = 'combo';
                            combo.textContent = count + ' 连击! 🔥';
                            document.body.appendChild(combo);
                            setTimeout(() => combo.remove(), 1000);
                        }

                        // 生成奖励物品
                        function spawnRewards() {
                            const rewards = ['🎁', '⭐', '💎', '🏆', '🎈', '🍭', '🎪', '🎯'];
                            for (let i = 0; i < 5; i++) {
                                setTimeout(() => {
                                    const reward = document.createElement('div');
                                    reward.className = 'reward';
                                    reward.textContent = rewards[Math.floor(Math.random() * rewards.length)];
                                    reward.style.left = (100 + Math.random() * (window.innerWidth - 200)) + 'px';
                                    reward.style.top = (100 + Math.random() * (window.innerHeight - 200)) + 'px';
                                    reward.style.animationDelay = Math.random() * 2 + 's';

                                    reward.addEventListener('click', (e) => {
                                        e.stopPropagation();
                                        reward.classList.add('collected');
                                        addScore(100, e.clientX, e.clientY);
                                        spawnClickParticles(e.clientX, e.clientY, '🎉');
                                        combo++;
                                        comboEl.textContent = combo;
                                        setTimeout(() => reward.remove(), 500);
                                    });

                                    document.body.appendChild(reward);

                                    // 10秒后自动消失
                                    setTimeout(() => {
                                        if (reward.parentNode) {
                                            reward.style.opacity = '0';
                                            setTimeout(() => reward.remove(), 300);
                                        }
                                    }, 10000);
                                }, i * 300);
                            }
                        }

                        // 特殊动作
                        function makeDance(type) {
                            dancer.classList.add(type);
                            spawnMusicNotes(dancerX, dancerY - 50);
                            addScore(5, dancerX, dancerY);
                            setTimeout(() => dancer.classList.remove(type), 1000);
                        }

                        // 自动跳舞和生成奖励
                        let autoDance = setInterval(() => {
                            if (Math.random() > 0.7) {
                                const actions = ['happy', 'waving'];
                                const action = actions[Math.floor(Math.random() * actions.length)];
                                dancer.classList.add(action);
                                setTimeout(() => dancer.classList.remove(action), 1000);
                            }
                        }, 2000);

                        // 鼠标靠近时小人开心
                        document.addEventListener('mousemove', (e) => {
                            const rect = dancerContainer.getBoundingClientRect();
                            const dist = Math.hypot(e.clientX - rect.left, e.clientY - rect.top);
                            if (dist < 100) {
                                dancer.classList.add('happy');
                            } else {
                                dancer.classList.remove('happy');
                            }
                        });

                        // 初始生成一些奖励
                        setTimeout(spawnRewards, 2000);

                        // 更新连击显示
                        setInterval(() => {
                            comboEl.textContent = combo;
                            if (Date.now() - lastClickTime > 2000) {
                                combo = 0;
                            }
                        }, 100);
                    </script>
                </body>
                </html>
                """);
        }
    }
}
