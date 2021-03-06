package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.msg.MessageType;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 12:47 PM
 */
public interface DialogData {

    @NotNull
    String getMessage();

    @NotNull
    MessageType getMessageType();

    @Nullable
    String getTitle();
}
