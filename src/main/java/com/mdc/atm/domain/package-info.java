@TypeDef(name = "MonetaryAmount",
        typeClass = MonetaryAmountUserType.class,
        defaultForType = MonetaryAmount.class)
package com.mdc.atm.domain;

import com.mdc.atm.repository.MonetaryAmountUserType;

import org.hibernate.annotations.TypeDef;

import javax.money.MonetaryAmount;
