package com.github.router.concurrent

import java.util.concurrent.Callable


interface ITask extends Callable<Void> {
}