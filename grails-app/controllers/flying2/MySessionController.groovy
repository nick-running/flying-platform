package flying2

import javax.servlet.http.HttpSessionEvent
import javax.servlet.http.HttpSessionListener

class MySessionController implements HttpSessionListener{

    def index() {}

    @Override
    void sessionCreated(HttpSessionEvent httpSessionEvent) {
        println "session created"
//        HttpSession session = httpSessionEvent.getSession();
//        ServletContext application = session.getServletContext();
//
//        // 在application范围由一个HashSet集保存所有的session
//        HashSet sessions = (HashSet) application.getAttribute("sessions");
//        if (sessions == null) {
//            sessions = new HashSet();
//            application.setAttribute("sessions", sessions);
//        }
//
//        // 新创建的session均添加到HashSet集中
//        sessions.add(session);
//        // 可以在别处从application范围中取出sessions集合
//        // 然后使用sessions.size()获取当前活动的session数，即为“在线人数”
    }

    @Override
    void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        println "session destroyed"
    }
}
