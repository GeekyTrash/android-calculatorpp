/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.math.MathRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: serso
 * Date: 10/30/11
 * Time: 1:03 AM
 */
public abstract class AbstractCalculatorMathRegistry<T extends MathEntity, P extends MathPersistenceEntity> implements CalculatorMathRegistry<T> {

	@NotNull
	private final MathRegistry<T> mathRegistry;

	@NotNull
	private final String prefix;

    @NotNull
    private final MathEntityDao<P> mathEntityDao;

	protected AbstractCalculatorMathRegistry(@NotNull MathRegistry<T> mathRegistry,
                                             @NotNull String prefix,
                                             @NotNull MathEntityDao<P> mathEntityDao) {
		this.mathRegistry = mathRegistry;
		this.prefix = prefix;
        this.mathEntityDao = mathEntityDao;
    }



    @NotNull
	protected abstract Map<String, String> getSubstitutes();

	@Nullable
	@Override
	public String getDescription(@NotNull String mathEntityName) {
		final String stringName;

		final Map<String, String> substitutes = getSubstitutes();
		final String substitute = substitutes.get(mathEntityName);
		if (substitute == null) {
			stringName = prefix + mathEntityName;
		} else {
			stringName = prefix + substitute;
		}

        return mathEntityDao.getDescription(stringName);
	}

    public synchronized void load() {
        final MathEntityPersistenceContainer<P> persistenceContainer = mathEntityDao.load();

        final List<P> notCreatedEntities = new ArrayList<P>();

        if (persistenceContainer != null) {
            for (P entity : persistenceContainer.getEntities()) {
                if (!contains(entity.getName())) {
                    try {
                        final JBuilder<? extends T> builder = createBuilder(entity);
                        add(builder);
                    } catch (RuntimeException e) {
                        Locator.getInstance().getLogger().error(null, e.getLocalizedMessage(), e);
                        notCreatedEntities.add(entity);
                    }
                }
            }
        }

        try {
            if (!notCreatedEntities.isEmpty()) {
                final StringBuilder errorMessage = new StringBuilder(notCreatedEntities.size() * 100);
                for (P notCreatedEntity : notCreatedEntities) {
                    errorMessage.append(notCreatedEntity).append("\n\n");
                }

                Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_message_dialog, MessageDialogData.newInstance(CalculatorMessages.newErrorMessage(CalculatorMessages.msg_007, errorMessage.toString()), null));
            }
        } catch (RuntimeException e) {
            // just in case
            Locator.getInstance().getLogger().error(null, e.getLocalizedMessage(), e);
        }
    }

    @NotNull
	protected abstract JBuilder<? extends T> createBuilder(@NotNull P entity);

    @Override
	public synchronized void save() {
        final MathEntityPersistenceContainer<P> container = createPersistenceContainer();

        for (T entity : this.getEntities()) {
            if (!entity.isSystem()) {
                final P persistenceEntity = transform(entity);
                if (persistenceEntity != null) {
                    container.getEntities().add(persistenceEntity);
                }
            }
        }

        this.mathEntityDao.save(container);
	}

	@Nullable
	protected abstract P transform(@NotNull T entity);

	@NotNull
	protected abstract MathEntityPersistenceContainer<P> createPersistenceContainer();

	@NotNull
	@Override
	public List<T> getEntities() {
		return mathRegistry.getEntities();
	}

	@NotNull
	@Override
	public List<T> getSystemEntities() {
		return mathRegistry.getSystemEntities();
	}

	@Override
	public T add(@NotNull JBuilder<? extends T> JBuilder) {
		return mathRegistry.add(JBuilder);
	}

	@Override
	public void remove(@NotNull T var) {
		mathRegistry.remove(var);
	}

	@NotNull
	@Override
	public List<String> getNames() {
		return mathRegistry.getNames();
	}

	@Override
	public boolean contains(@NotNull String name) {
		return mathRegistry.contains(name);
	}

	@Override
	public T get(@NotNull String name) {
		return mathRegistry.get(name);
	}

	@Override
	public T getById(@NotNull Integer id) {
		return mathRegistry.getById(id);
	}
}
