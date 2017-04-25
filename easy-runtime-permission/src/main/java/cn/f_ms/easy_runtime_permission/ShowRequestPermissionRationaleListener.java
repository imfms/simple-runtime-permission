package cn.f_ms.easy_runtime_permission;

/**
 * Show Request Permission RationaleListener
 *
 * @author f-ms
 * @time 2017/4/25
 */

public interface ShowRequestPermissionRationaleListener {


    /**
     * Show Request Permission Rationale Controler
     */
    public interface ShowRequestPermissionRationaleControler {

        /** When User Agree */
        void doContinue();

        /** When User Refuse */
        void doCancel();
    }

    /**
     * When Need Show Request
     * @param controler    controler
     */
    void onShowRequestPermissionRationale(ShowRequestPermissionRationaleControler controler);
}
