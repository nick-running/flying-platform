package flying2


class BackendInterceptor {

    boolean before() {
        if (!session.backUser) {
            redirect(controller: 'account', action: 'backLogin', params: [isAlert: true])
            return false
        }
        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }

}
