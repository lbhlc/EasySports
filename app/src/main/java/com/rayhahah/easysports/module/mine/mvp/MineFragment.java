package com.rayhahah.easysports.module.mine.mvp;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.rayhahah.dialoglib.DialogInterface;
import com.rayhahah.dialoglib.MDAlertDialog;
import com.rayhahah.dialoglib.MDEditDialog;
import com.rayhahah.easysports.R;
import com.rayhahah.easysports.app.MyApplication;
import com.rayhahah.easysports.common.BaseFragment;
import com.rayhahah.easysports.common.C;
import com.rayhahah.easysports.databinding.FragmentMineBinding;
import com.rayhahah.easysports.module.home.HomeActivity;
import com.rayhahah.easysports.module.mine.bean.MineListBean;
import com.rayhahah.easysports.module.mine.business.teamplayer.SingleListActivity;
import com.rayhahah.easysports.module.mine.domain.MineListAdapter;
import com.rayhahah.easysports.view.TextListItemDecoration;
import com.rayhahah.rbase.utils.base.CacheUtils;
import com.rayhahah.rbase.utils.base.DialogUtil;
import com.rayhahah.rbase.utils.base.StringUtils;
import com.rayhahah.rbase.utils.base.ToastUtils;
import com.rayhahah.rbase.utils.useful.SPManager;

import java.util.List;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by a on 2017/5/17.
 */

public class MineFragment extends BaseFragment<MinePresenter, FragmentMineBinding>
        implements MineContract.IMineView, BaseQuickAdapter.OnItemChildClickListener {

    private List<MineListBean> mData;
    private MineListAdapter mMineListAdapter;

    @Override
    protected int setFragmentLayoutRes() {
        return R.layout.fragment_mine;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mBinding.toolbar.tvToolbarTitle.setText(getResources().getString(R.string.mine));
        initRv();
    }

    private void initRv() {
        mData = mPresenter.getMineListData();
        mMineListAdapter = new MineListAdapter(mData) {
            @Override
            public void setItemCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!MyApplication.isNightTheme()) {
                    SPManager.get().putString(C.SP.THEME, C.TRUE);
                    getActivity().setTheme(R.style.AppNightTheme);
                } else {
                    SPManager.get().putString(C.SP.THEME, C.FALSE);
                    getActivity().setTheme(R.style.AppDayTheme);
                }
                refreshUI();
            }
        };
        mBinding.rvMineList.setAdapter(mMineListAdapter);
        TextListItemDecoration<MineListBean> decor = new TextListItemDecoration<>(getActivity(), mData
                , mThemeColorMap.get(C.ATTRS.COLOR_TEXT_DARK)
                , mThemeColorMap.get(C.ATTRS.COLOR_BG_DARK)
                , mThemeColorMap.get(C.ATTRS.COLOR_TEXT_DARK)
                , TextListItemDecoration.GRAVITY_LEFT
                , new TextListItemDecoration.DecorationCallback() {
            @Override
            public String getGroupId(int position) {
                if (position < mData.size()
                        && StringUtils.isNotEmpty(mData.get(position).getSectionData())) {
                    return mData.get(position).getSectionData();
                }
                return null;
            }

            @Override
            public String getGroupFirstLine(int position) {
                if (position < mData.size()
                        && StringUtils.isNotEmpty(mData.get(position).getSectionData())) {
                    return mData.get(position).getSectionData();
                }
                return "";
            }

            @Override
            public String getActiveGroup() {
                return "";
            }
        });
        mMineListAdapter.setOnItemChildClickListener(this);
        mBinding.rvMineList.addItemDecoration(decor);
        mBinding.rvMineList.setLayoutManager(new LinearLayoutManager(getActivity()
                , LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected MinePresenter getPresenter() {
        return new MinePresenter(this);
    }

    @Override
    public void showViewLoading() {

    }

    @Override
    public void showViewError(Throwable t) {

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        List<MineListBean> data = adapter.getData();
        MineListBean bean = data.get(position);
        String isLogin = SPManager.get().getStringValue(C.SP.IS_LOGIN, C.FALSE);
        switch (bean.getId()) {
            //登陆与注册
            case C.MINE.ID_LOGIN:
                if (C.TRUE.equals(isLogin)) {

                } else {

                }
                break;
            //清除缓存
            case C.MINE.ID_CLEAN:
                MDAlertDialog dialog = initCleanDialog();
                dialog.show();
                break;
            //所有球队
            case C.MINE.ID_TEAM:
                SingleListActivity.start(getActivity(), getActivity(), SingleListActivity.TYPE_TEAM);
                break;
            //所有球员
            case C.MINE.ID_PLAYER:
                SingleListActivity.start(getActivity(), getActivity(), SingleListActivity.TYPE_PLAYER);
                break;
            //反馈信息
            case C.MINE.ID_FEEDBACK:
                if (C.TRUE.equals(isLogin)) {
                    MDEditDialog mdEditDialog = initFeedbackDialog();
                    mdEditDialog.show();
                } else {
                    ToastUtils.showShort("请先登录~");
                }
                break;
            //关于我们
            case C.MINE.ID_ABOUT:

                break;
            default:
                break;
        }
    }


    /**
     * 切换皮肤刷新UI
     */
    private void refreshUI() {
        SPManager.get().putString(C.SP.TAG_MINE_SELECTED, C.TRUE);
//        getActivity().overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        HomeActivity.start(getActivity(), getActivity());
    }

    /**
     * 初始化清除缓存dialog
     */
    private MDAlertDialog initCleanDialog() {
        return new MDAlertDialog.Builder(getActivity())
                .setDialogBgResource(R.drawable.shape_color_bg_corner4)
                .setTitleVisible(false)
                .setContentText(getResources().getString(R.string.mine_clean_ask))
                .setContentTextColor(mThemeColorMap.get(C.ATTRS.COLOR_TEXT_DARK))
                .setContentTextSize(20)
                .setButtonTextSize(16)
                .setLeftButtonTextColor(mThemeColorMap.get(C.ATTRS.COLOR_PRIMARY))
                .setRightButtonTextColor(mThemeColorMap.get(C.ATTRS.COLOR_PRIMARY))
                .setOnclickListener(new DialogInterface.OnLeftAndRightClickListener<MDAlertDialog>() {
                    @Override
                    public void clickLeftButton(MDAlertDialog dialog, View view) {
                        dialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(MDAlertDialog dialog, View view) {
                        CacheUtils.cleanApplicationCache(getActivity());
                        ToastUtils.showShort(getResources().getString(R.string.mine_clean_success));
                        mMineListAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setButtonBgResource(R.drawable.selector_md_dialog_color_primary)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    /**
     * 初始化意见反馈弹窗
     */
    private MDEditDialog initFeedbackDialog() {
        MDEditDialog mdEditDialog = new MDEditDialog.Builder(getActivity())
                .setDialogBgResource(R.drawable.shape_color_bg_corner4)
                .setTitleTextSize(20)
                .setTitleTextColor(mThemeColorMap.get(C.ATTRS.COLOR_PRIMARY))
                .setTitleText(getResources().getString(R.string.mine_feedback_title))
                .setContentTextColor(mThemeColorMap.get(C.ATTRS.COLOR_TEXT_DARK))
                .setContentTextSize(15)
                .setInputTpye(InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .setLineColor(mThemeColorMap.get(C.ATTRS.COLOR_PRIMARY))
                .setLineViewVisibility(View.GONE)
                //屏幕数值方向百分比
                .setMinHeight((float) 0.5)
                .setHintTextColor(mThemeColorMap.get(C.ATTRS.COLOR_TEXT_LIGHT))
                .setHintText(getResources().getString(R.string.mine_feedback_hint))
                .setButtonTextSize(20)
                .setLeftButtonTextColor(mThemeColorMap.get(C.ATTRS.COLOR_PRIMARY))
                .setRightButtonTextColor(mThemeColorMap.get(C.ATTRS.COLOR_PRIMARY))
                .setOnclickListener(new DialogInterface.OnLeftAndRightClickListener<MDEditDialog>() {
                    @Override
                    public void clickLeftButton(MDEditDialog dialog, View view) {
                        dialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(final MDEditDialog dialog, View view) {
                        ToastUtils.showShort(dialog.getEditTextContent());
                        dialog.dismiss();
                        DialogUtil.showLoadingDialog(getActivity(), "提交ing");
                        mPresenter.uploadFeedback(dialog.getEditTextContent());
                    }
                })
                .setButtonBgResource(R.drawable.selector_md_dialog_color_primary)
                .setCanceledOnTouchOutside(false)
                .build();
        return mdEditDialog;
    }

    @Override
    public void uploadFeedbackDone(BmobException e) {
        DialogUtil.dismissDialog();
        if (e == null) {
            ToastUtils.showShort("感谢您的建议！");
        } else {
            ToastUtils.showShort("提交失败~");
        }
    }
}